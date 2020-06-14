package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

@ApplicationScoped
public class KafkaEventConsumer {

    private final static Logger LOGGER = Logger.getLogger(KafkaEventConsumer.class.getName());

    private final SecretStore secretStore;
    private final KafkaAggregateRootEventConsumedRepository kafkaEventConsumedRepository;
    private final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer;
    private final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer;
    private final AggregateRootMaterializedStateConsumerDeserializer aggregateRootMaterializedStateConsumerDeserializer;
    private final UserTransaction transaction;
    private final Instance<AggregateRootEventConsumer> eventConsumersBeans;
    private final String gitCommitId;
    private final Executor executor;
    private final CreatedAtProvider createdAtProvider;

    public KafkaEventConsumer(final SecretStore secretStore,
                              final KafkaAggregateRootEventConsumedRepository kafkaEventConsumedRepository,
                              final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer,
                              final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer,
                              final AggregateRootMaterializedStateConsumerDeserializer aggregateRootMaterializedStateConsumerDeserializer,
                              final UserTransaction transaction,
                              @Any final Instance<AggregateRootEventConsumer> eventConsumersBeans,
                              final CreatedAtProvider createdAtProvider) {
        this.secretStore = Objects.requireNonNull(secretStore);
        this.kafkaEventConsumedRepository = Objects.requireNonNull(kafkaEventConsumedRepository);
        this.aggregateRootEventPayloadConsumerDeserializer = Objects.requireNonNull(aggregateRootEventPayloadConsumerDeserializer);
        this.aggregateRootEventMetadataConsumerDeSerializer = Objects.requireNonNull(aggregateRootEventMetadataConsumerDeSerializer);
        this.aggregateRootMaterializedStateConsumerDeserializer = Objects.requireNonNull(aggregateRootMaterializedStateConsumerDeserializer);
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
        LOGGER.log(Level.INFO, String.format("Receiving record '%s'", record.getKey().toString()));
        return CompletableFuture.supplyAsync(() -> {
            boolean processedSuccessfully = true;
            do {
                try {
                    final DebeziumAggregateRootEventConsumable decryptableAggregateRootEvent = new DebeziumAggregateRootEventConsumable(record);
                    final String aggregateRootType = decryptableAggregateRootEvent.eventId().aggregateRootId().aggregateRootType();
                    final String aggregateRootId = decryptableAggregateRootEvent.eventId().aggregateRootId().aggregateRootId();
                    final Optional<AggregateRootSecret> aggregateRootSecret = secretStore.read(aggregateRootType, aggregateRootId);
                    final AggregateRootEventId aggregateRootEventId = decryptableAggregateRootEvent.eventId();
                    if (!kafkaEventConsumedRepository.hasFinishedConsumingEvent(aggregateRootEventId)) {
                        final String eventType = decryptableAggregateRootEvent.eventType();
                        final List<AggregateRootEventConsumer> consumersToProcessEvent = eventConsumersBeans.stream()
                                .filter(eventConsumer -> aggregateRootType.equals(eventConsumer.aggregateRootType()))
                                .filter(eventConsumer -> eventType.equals(eventConsumer.eventType()))
                                .collect(Collectors.toList());
                        for (final AggregateRootEventConsumer consumerToProcessEvent: consumersToProcessEvent) {
                            final AggregateRootEventConsumable aggregateRootEventConsumable = new DecryptedAggregateRootEventConsumable(decryptableAggregateRootEvent, aggregateRootSecret,
                                    aggregateRootEventMetadataConsumerDeSerializer, aggregateRootEventPayloadConsumerDeserializer, aggregateRootMaterializedStateConsumerDeserializer);
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
                        LOGGER.log(Level.INFO, String.format("Event '%s' already consumed", aggregateRootEventId));
                    }
                } catch (final UnableToDecodeDebeziumEventMessageException unableToDecodeDebeziumEventMessageException) {
                    LOGGER.log(Level.SEVERE, String.format("Unable to decode debezium event message in topic '%s' in partition '%d' in offset '%d' get message '%s'. Will try once again.",
                            unableToDecodeDebeziumEventMessageException.topic(),
                            unableToDecodeDebeziumEventMessageException.partition(),
                            unableToDecodeDebeziumEventMessageException.offset(),
                            unableToDecodeDebeziumEventMessageException.getMessage()));
                    processedSuccessfully = false;
                    waitSomeTime();
                } catch (final Exception exception) {
                    LOGGER.log(Level.SEVERE, "Message processing failure. Will try once again.", exception);
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
