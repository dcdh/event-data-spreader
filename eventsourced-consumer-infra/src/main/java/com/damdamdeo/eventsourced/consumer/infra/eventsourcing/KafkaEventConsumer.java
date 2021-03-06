package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInKeyRecord;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInPayloadRecord;
import com.damdamdeo.eventsourced.encryption.api.JsonbCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import io.quarkus.runtime.Startup;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.io.StringReader;
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

@Startup
@ApplicationScoped
public class KafkaEventConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final JsonbCryptoService jsonCryptoService;
    private final KafkaAggregateRootEventConsumedRepository kafkaEventConsumedRepository;
    private final UserTransaction transaction;
    private final Instance<AggregateRootEventConsumer<JsonObject>> eventConsumersBeans;
    private final String gitCommitId;
    private final Executor executor;
    private final CreatedAtProvider createdAtProvider;

    public KafkaEventConsumer(final JsonbCryptoService jsonCryptoService,
                              final KafkaAggregateRootEventConsumedRepository kafkaEventConsumedRepository,
                              final UserTransaction transaction,
                              @Any final Instance<AggregateRootEventConsumer<JsonObject>> eventConsumersBeans,
                              final CreatedAtProvider createdAtProvider) {
        this.jsonCryptoService = Objects.requireNonNull(jsonCryptoService);
        this.kafkaEventConsumedRepository = Objects.requireNonNull(kafkaEventConsumedRepository);
        this.transaction = Objects.requireNonNull(transaction);
        this.eventConsumersBeans = Objects.requireNonNull(eventConsumersBeans);
        this.executor = Executors.newSingleThreadExecutor();
        this.createdAtProvider = createdAtProvider;
        final InputStream gitProperties = this.getClass().getResourceAsStream("/git.properties");
        try (final Scanner scanner = new Scanner(gitProperties).useDelimiter("\\Z");
             final JsonReader reader = Json.createReader(new StringReader(scanner.next()))) {
            final JsonObject gitPropertiesObject = reader.readObject();
            LOGGER.info(String.format("Git build properties '%s'", gitPropertiesObject));
            this.gitCommitId = Objects.requireNonNull(gitPropertiesObject.getString("git.commit.id"));
        }
    }

    @Incoming("event-in")
    @Transactional(Transactional.TxType.NEVER)
    public CompletionStage<Void> onMessage(final IncomingKafkaRecord<DebeziumJsonbEventInKeyRecord, DebeziumJsonbEventInPayloadRecord> record) {
        LOGGER.info(String.format("Receiving record '%s'", record.getKey().toString()));
        return CompletableFuture.supplyAsync(() -> {
            boolean processedSuccessfully = true;
            do {
                try {
                    final DebeziumOperation debeziumOperation = DebeziumOperation.fromDebeziumOperation(record);
                    final DebeziumJsonbAggregateRootEventConsumable debeziumJsonbAggregateRootEventConsumable = record.getPayload().debeziumJsonbAggregateRootEventConsumable();
                    final String aggregateRootType = debeziumJsonbAggregateRootEventConsumable.eventId().aggregateRootId().aggregateRootType();
                    final AggregateRootEventId aggregateRootEventId = debeziumJsonbAggregateRootEventConsumable.eventId();
                    if (!kafkaEventConsumedRepository.hasFinishedConsumingEvent(aggregateRootEventId)) {
                        final String eventType = debeziumJsonbAggregateRootEventConsumable.eventType();
                        final List<AggregateRootEventConsumer> consumersToProcessEvent = eventConsumersBeans.stream()
                                .filter(eventConsumer -> aggregateRootType.equals(eventConsumer.aggregateRootType()))
                                .filter(eventConsumer -> eventType.equals(eventConsumer.eventType()))
                                .collect(Collectors.toList());
                        final KafkaInfrastructureMetadata kafkaInfrastructureMetadata = new IncomingKafkaRecordKafkaInfrastructureMetadata(record);
                        for (final AggregateRootEventConsumer consumerToProcessEvent: consumersToProcessEvent) {
                            final AggregateRootEventConsumable aggregateRootEventConsumable = DecryptedAggregateRootEventConsumable.newBuilder()
                                    .withDebeziumJsonbAggregateRootEventConsumable(debeziumJsonbAggregateRootEventConsumable)
                                    .build(jsonCryptoService);
                            final List<String> consumersHavingProcessedEventClassNames = kafkaEventConsumedRepository.getConsumersHavingProcessedEvent(aggregateRootEventConsumable.eventId());
                            if (!consumersHavingProcessedEventClassNames.contains(consumerToProcessEvent.getClass().getName())) {
                                transaction.begin();// needed however exception will be thrown even if the consumer is marked with @Transactional
                                consumerToProcessEvent.consume(aggregateRootEventConsumable, debeziumOperation.operation());
                                kafkaEventConsumedRepository.addEventConsumerConsumed(aggregateRootEventConsumable.eventId(),
                                        consumerToProcessEvent.getClass(),
                                        createdAtProvider.createdAt(),
                                        kafkaInfrastructureMetadata,
                                        gitCommitId);
                                transaction.commit();
                            }
                        }
                        kafkaEventConsumedRepository.markEventAsConsumed(aggregateRootEventId, createdAtProvider.createdAt(), kafkaInfrastructureMetadata);
                    } else {
                        LOGGER.info(String.format("Event '%s' already consumed", aggregateRootEventId));
                    }
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
                }
            } while (!processedSuccessfully);
            record.ack();
            return null;
        }, executor);
    }

}
