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
    private final EventConsumedRepository eventConsumedRepository;
    private final EventPayloadDeserializer eventPayloadDeserializer;
    private final EventMetadataDeserializer eventMetadataDeserializer;
    private final UserTransaction transaction;
    private final Instance<EventConsumer> eventConsumersBeans;
    private final String gitCommitId;
    private final Executor executor;

    public KafkaEventConsumer(final SecretStore secretStore,
                              final EventConsumedRepository eventConsumedRepository,
                              final EventPayloadDeserializer eventPayloadDeserializer,
                              final EventMetadataDeserializer eventMetadataDeserializer,
                              final UserTransaction transaction,
                              @Any final Instance<EventConsumer> eventConsumersBeans) {
        this.secretStore = Objects.requireNonNull(secretStore);
        this.eventConsumedRepository = Objects.requireNonNull(eventConsumedRepository);
        this.eventPayloadDeserializer = Objects.requireNonNull(eventPayloadDeserializer);
        this.eventMetadataDeserializer = Objects.requireNonNull(eventMetadataDeserializer);
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
                processedSuccessfully = true;
                try {
                    final DebeziumEventKafkaMessage debeziumEventKafkaMessage = new DebeziumEventKafkaMessage(record);
                    final Optional<EncryptedEventSecret> encryptedEventSecret = secretStore.read(debeziumEventKafkaMessage.aggregateRootType(),
                            debeziumEventKafkaMessage.aggregateRootId());
                    final DecryptableEvent decryptableEvent = debeziumEventKafkaMessage;
                    final EventId eventId = decryptableEvent.eventId();
                    if (!eventConsumedRepository.hasConsumedEvent(eventId)) {
                        final String aggregateRootType = decryptableEvent.eventId().aggregateRootType();
                        final String eventType = decryptableEvent.eventType();

                        final List<EventConsumer> eventConsumersToExecute = eventConsumersBeans.stream()
                                .filter(eventConsumer -> aggregateRootType.equals(eventConsumer.aggregateRootType()))
                                .filter(eventConsumer -> eventType.equals(eventConsumer.eventType()))
                                .collect(Collectors.toList());
                        for (final EventConsumer eventConsumerToExecute: eventConsumersToExecute) {
                            final Event event = new Event(decryptableEvent, encryptedEventSecret, eventMetadataDeserializer, eventPayloadDeserializer);
                            final List<String> consumedEventClassNames = eventConsumedRepository.getConsumedEventsForEventId(event.eventId());
                            if (!consumedEventClassNames.contains(eventConsumerToExecute.getClass().getName())) {
                                transaction.begin();// needed however exception will be thrown even if the consumer is marked with @Transactional
                                eventConsumerToExecute.consume(event);
                                eventConsumedRepository.addEventConsumerConsumed(event.eventId(),
                                        eventConsumerToExecute.getClass(),
                                        new ConsumerRecordKafkaSource(record),
                                        gitCommitId);
                                transaction.commit();
                            }
                        }
                        eventConsumedRepository.markEventAsConsumed(eventId, new Date(), new ConsumerRecordKafkaSource(record));
                    } else {
                        LOGGER.log(Level.INFO, String.format("Event '%s' already consumed", eventId));
                    }
                } catch (final UnableToDecodeDebeziumEventMessageException unableToDecodeDebeziumEventMessageException) {
                    waitSomeTime();
                    LOGGER.log(Level.WARNING, String.format("Unable to decode debezium event message in topic '%s' in partition '%d' in offset '%d' get message '%s'. Will try once again.",
                            unableToDecodeDebeziumEventMessageException.topic(),
                            unableToDecodeDebeziumEventMessageException.partition(),
                            unableToDecodeDebeziumEventMessageException.offset(),
                            unableToDecodeDebeziumEventMessageException.getMessage()));
                    processedSuccessfully = false;
                } catch (final Exception exception) {
                    waitSomeTime();
                    LOGGER.log(Level.WARNING, "Message processing failure. Will try once again.", exception);
                    processedSuccessfully = false;
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
