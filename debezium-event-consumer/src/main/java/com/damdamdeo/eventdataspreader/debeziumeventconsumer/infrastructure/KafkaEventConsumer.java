package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.*;
import com.damdamdeo.eventdataspreader.event.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.json.JsonReader;
import javax.transaction.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;

@ApplicationScoped
public class KafkaEventConsumer {

    private final static Logger LOGGER = Logger.getLogger(KafkaEventConsumer.class.getName());

    private final SecretStore secretStore;
    private final KafkaEventConsumedRepository kafkaEventConsumedRepository;
    private final EventPayloadDeserializer eventPayloadDeserializer;
    private final EventMetadataDeSerializer eventMetadataDeSerializer;
    private final UserTransaction transaction;
    private final Instance<EventConsumer> eventConsumersBeans;
    private final String gitCommitId;
    private final Executor executor;

    public KafkaEventConsumer(final SecretStore secretStore,
                              final KafkaEventConsumedRepository kafkaEventConsumedRepository,
                              final EventPayloadDeserializer eventPayloadDeserializer,
                              final EventMetadataDeSerializer eventMetadataDeSerializer,
                              final UserTransaction transaction,
                              @Any final Instance<EventConsumer> eventConsumersBeans) {
        this.secretStore = Objects.requireNonNull(secretStore);
        this.kafkaEventConsumedRepository = Objects.requireNonNull(kafkaEventConsumedRepository);
        this.eventPayloadDeserializer = Objects.requireNonNull(eventPayloadDeserializer);
        this.eventMetadataDeSerializer = Objects.requireNonNull(eventMetadataDeSerializer);
        this.transaction = Objects.requireNonNull(transaction);
        this.eventConsumersBeans = Objects.requireNonNull(eventConsumersBeans);
        this.executor = Executors.newSingleThreadExecutor();
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
        LOGGER.log(Level.FINE, String.format("Receiving record '%s'", record.getKey().toString()));
        return CompletableFuture.supplyAsync(() -> {
            boolean processedSuccessfully = true;
            do {
                try {
                    final DebeziumEventKafkaMessage debeziumEventKafkaMessage = new DebeziumEventKafkaMessage(record);
                    final Optional<EncryptedEventSecret> encryptedEventSecret = secretStore.read(debeziumEventKafkaMessage.aggregateRootType(),
                            debeziumEventKafkaMessage.aggregateRootId());
                    final DecryptableEvent decryptableEvent = debeziumEventKafkaMessage;
                    final EventId eventId = decryptableEvent.eventId();
                    if (!kafkaEventConsumedRepository.hasFinishedConsumingEvent(eventId)) {
                        final String aggregateRootType = decryptableEvent.eventId().aggregateRootType();
                        final String eventType = decryptableEvent.eventType();

                        final List<EventConsumer> consumersToProcessEvent = eventConsumersBeans.stream()
                                .filter(eventConsumer -> aggregateRootType.equals(eventConsumer.aggregateRootType()))
                                .filter(eventConsumer -> eventType.equals(eventConsumer.eventType()))
                                .collect(Collectors.toList());
                        for (final EventConsumer consumerToProcessEvent: consumersToProcessEvent) {
                            final Event event = new DefaultEvent(decryptableEvent, encryptedEventSecret, eventMetadataDeSerializer, eventPayloadDeserializer);
                            final List<String> consumersHavingProcessedEventClassNames = kafkaEventConsumedRepository.getConsumersHavingProcessedEvent(event.eventId());
                            if (!consumersHavingProcessedEventClassNames.contains(consumerToProcessEvent.getClass().getName())) {
                                transaction.begin();// needed however exception will be thrown even if the consumer is marked with @Transactional
                                consumerToProcessEvent.consume(event);
                                kafkaEventConsumedRepository.addEventConsumerConsumed(event.eventId(),
                                        consumerToProcessEvent.getClass(),
                                        LocalDateTime.now(),
                                        new ConsumerRecordKafkaSource(record),
                                        gitCommitId);
                                transaction.commit();
                            }
                        }
                        kafkaEventConsumedRepository.markEventAsConsumed(eventId, LocalDateTime.now(), new ConsumerRecordKafkaSource(record));
                    } else {
                        LOGGER.log(Level.INFO, String.format("Event '%s' already consumed", eventId));
                    }
                } catch (final UnableToDecodeDebeziumEventMessageException unableToDecodeDebeziumEventMessageException) {
                    LOGGER.log(Level.WARNING, String.format("Unable to decode debezium event message in topic '%s' in partition '%d' in offset '%d' get message '%s'. Will try once again.",
                            unableToDecodeDebeziumEventMessageException.topic(),
                            unableToDecodeDebeziumEventMessageException.partition(),
                            unableToDecodeDebeziumEventMessageException.offset(),
                            unableToDecodeDebeziumEventMessageException.getMessage()));
                    processedSuccessfully = false;
                    waitSomeTime();
                } catch (final Exception exception) {
                    LOGGER.log(Level.WARNING, "Message processing failure. Will try once again.", exception);
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
