package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.encryption.api.AESEncryptionQualifier;
import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

@ApplicationScoped
public class KafkaEventConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final CryptoService<JsonNode> jsonCryptoService;
    private final Encryption encryption;
    private final KafkaAggregateRootEventConsumedRepository kafkaEventConsumedRepository;
    private final UserTransaction transaction;
    private final Instance<AggregateRootEventConsumer<JsonNode>> eventConsumersBeans;
    private final String gitCommitId;
    private final Executor executor;
    private final CreatedAtProvider createdAtProvider;

    public KafkaEventConsumer(final CryptoService<JsonNode> jsonCryptoService,
                              @AESEncryptionQualifier final Encryption encryption,
                              final KafkaAggregateRootEventConsumedRepository kafkaEventConsumedRepository,
                              final UserTransaction transaction,
                              @Any final Instance<AggregateRootEventConsumer<JsonNode>> eventConsumersBeans,
                              final CreatedAtProvider createdAtProvider) {
        this.objectMapper = new ObjectMapper();
        this.jsonCryptoService = Objects.requireNonNull(jsonCryptoService);
        this.encryption = Objects.requireNonNull(encryption);
        this.kafkaEventConsumedRepository = Objects.requireNonNull(kafkaEventConsumedRepository);
        this.transaction = Objects.requireNonNull(transaction);
        this.eventConsumersBeans = Objects.requireNonNull(eventConsumersBeans);
        this.executor = Executors.newSingleThreadExecutor();
        this.createdAtProvider = createdAtProvider;
        try (final InputStream gitProperties = getClass().getClassLoader().getResourceAsStream("git.properties");
             final JsonReader reader = Json.createReader(gitProperties)) {
            final javax.json.JsonObject gitPropertiesObject = reader.readObject();
            this.gitCommitId = Objects.requireNonNull(gitPropertiesObject.getString("git.commit.id"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Incoming("event-in")
    @Transactional(Transactional.TxType.NEVER)
    public CompletionStage<Void> onMessage(final IncomingKafkaRecord<JsonObject, JsonObject> record) {
        LOGGER.info(String.format("Receiving record '%s'", record.getKey().toString()));
        return CompletableFuture.supplyAsync(() -> {
            boolean processedSuccessfully = true;
            do {
                try {
                    final DebeziumAggregateRootEventConsumable debeziumAggregateRootEventConsumable = new DebeziumAggregateRootEventConsumable(record, objectMapper);
                    final String aggregateRootType = debeziumAggregateRootEventConsumable.eventId().aggregateRootId().aggregateRootType();
                    final AggregateRootEventId aggregateRootEventId = debeziumAggregateRootEventConsumable.eventId();
                    if (!kafkaEventConsumedRepository.hasFinishedConsumingEvent(aggregateRootEventId)) {
                        final String eventType = debeziumAggregateRootEventConsumable.eventType();
                        final List<AggregateRootEventConsumer> consumersToProcessEvent = eventConsumersBeans.stream()
                                .filter(eventConsumer -> aggregateRootType.equals(eventConsumer.aggregateRootType()))
                                .filter(eventConsumer -> eventType.equals(eventConsumer.eventType()))
                                .collect(Collectors.toList());
                        for (final AggregateRootEventConsumer consumerToProcessEvent: consumersToProcessEvent) {
                            final AggregateRootEventConsumable aggregateRootEventConsumable = DecryptedAggregateRootEventConsumable.newBuilder()
                                    .withDebeziumAggregateRootEventConsumable(debeziumAggregateRootEventConsumable)
                                    .build(jsonCryptoService, encryption);
                            final List<String> consumersHavingProcessedEventClassNames = kafkaEventConsumedRepository.getConsumersHavingProcessedEvent(aggregateRootEventConsumable.eventId());
                            if (!consumersHavingProcessedEventClassNames.contains(consumerToProcessEvent.getClass().getName())) {
                                transaction.begin();// needed however exception will be thrown even if the consumer is marked with @Transactional
                                consumerToProcessEvent.consume(aggregateRootEventConsumable);
                                kafkaEventConsumedRepository.addEventConsumerConsumed(aggregateRootEventConsumable.eventId(),
                                        consumerToProcessEvent.getClass(),
                                        createdAtProvider.createdAt(),
                                        new ConsumerRecordKafkaInfrastructureMetadata(record),
                                        gitCommitId);
                                transaction.commit();
                            }
                        }
                        kafkaEventConsumedRepository.markEventAsConsumed(aggregateRootEventId, createdAtProvider.createdAt(), new ConsumerRecordKafkaInfrastructureMetadata(record));
                    } else {
                        LOGGER.info(String.format("Event '%s' already consumed", aggregateRootEventId));
                    }
                } catch (final UnableToDecodeDebeziumEventMessageException unableToDecodeDebeziumEventMessageException) {
                    LOGGER.error(String.format("Unable to decode debezium event message in topic '%s' in partition '%d' in offset '%d' get message '%s'. Will try once again.",
                            unableToDecodeDebeziumEventMessageException.topic(),
                            unableToDecodeDebeziumEventMessageException.partition(),
                            unableToDecodeDebeziumEventMessageException.offset(),
                            unableToDecodeDebeziumEventMessageException.getMessage()));
                    processedSuccessfully = false;
                    waitSomeTime();
                } catch (final UnsupportedDebeziumOperationException unsupportedDebeziumOperationException) {
                    LOGGER.warn(String.format("Unsupported Debezium operation to decode in topic '%s' in partition '%d' in offset '%d' get key '%s' and payload '%s'. Will not try.",
                            unsupportedDebeziumOperationException.topic(),
                            unsupportedDebeziumOperationException.partition(),
                            unsupportedDebeziumOperationException.offset(),
                            unsupportedDebeziumOperationException.key(),
                            unsupportedDebeziumOperationException.payload()));
                } catch (final Exception exception) {
                    LOGGER.error("Message processing failure. Will try once again.", exception);
                    processedSuccessfully = false;
                    waitSomeTime();
                }
            } while (!processedSuccessfully);
            return null;
        }, executor);
    }

    private void waitSomeTime() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
