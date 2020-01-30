package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.*;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.DecryptableEvent;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayloadDeserializer;
import io.smallrye.reactive.messaging.kafka.ReceivedKafkaMessage;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
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
import javax.json.Json;

@ApplicationScoped
public class KafkaEventConsumer {

    private final static Logger LOGGER = Logger.getLogger(KafkaEventConsumer.class.getName());

    @Inject
    EventConsumedRepository eventConsumedRepository;

    @Inject
    EncryptedEventSecretRepository encryptedEventSecretRepository;

    @Inject
    EventPayloadDeserializer eventPayloadDeserializer;

    @Inject
    EventMetadataDeserializer eventMetadataDeserializer;

    @Inject
    UserTransaction transaction;

    @Inject
    @Any
    Instance<EventConsumer> eventConsumersBeans;

    private String gitCommitId;

    @PostConstruct
    void onPostConstruct() {
        try (final InputStream gitProperties = getClass().getClassLoader().getResourceAsStream("git.properties");
             final JsonReader reader = Json.createReader(gitProperties)) {
            final javax.json.JsonObject gitPropertiesObject = reader.readObject();
            this.gitCommitId = Objects.requireNonNull(gitPropertiesObject.getString("git.commit.id"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Incoming("event")
    @Transactional(Transactional.TxType.NEVER)
    public CompletionStage<Void> onMessage(final ReceivedKafkaMessage<JsonObject, JsonObject> message) {
        LOGGER.log(Level.FINE, String.format("Receiving event '%s'", message.getKey().toString()));
        return CompletableFuture.supplyAsync(() -> {
            try {
                final DebeziumEventKafkaMessage debeziumEventKafkaMessage = new DebeziumEventKafkaMessage(message);
                switch (debeziumEventKafkaMessage.encryptedEventType()) {
                    case SECRET:// can be a DELETED_SECRET !
                        encryptedEventSecretRepository.merge(debeziumEventKafkaMessage);
                        break;
                    case ENCRYPTED_EVENT:
                        final EncryptedEventSecret encryptedEventSecret = encryptedEventSecretRepository.find(debeziumEventKafkaMessage.aggregateRootId(),
                                debeziumEventKafkaMessage.aggregateRootType());
                        final DecryptableEvent decryptableEvent = debeziumEventKafkaMessage;
                        final String eventId = decryptableEvent.eventId();
                        if (!eventConsumedRepository.hasConsumedEvent(eventId)) {
                            final String aggregateRootType = decryptableEvent.aggregateRootType();
                            final String eventType = decryptableEvent.eventType();
                            final Instance<EventConsumer> eventConsumers = eventConsumersBeans.select(EventConsumer.class, new EventQualifierLiteral(
                                    aggregateRootType,
                                    eventType));
                            if (eventConsumers.isResolvable()) {
                                final Event event = new Event(decryptableEvent, encryptedEventSecret, eventMetadataDeserializer, eventPayloadDeserializer);
                                final List<String> consumedEventClassNames = eventConsumedRepository.getConsumedEventsForEventId(event.eventId());
                                for (final EventConsumer eventConsumer : eventConsumers) {
                                    if (!consumedEventClassNames.contains(eventConsumer.getClass().getName())) {
                                        transaction.begin();
                                        eventConsumer.consume(event);
                                        eventConsumedRepository.addEventConsumerConsumed(event.eventId(),
                                                eventConsumer.getClass(),
                                                new ConsumerRecordKafkaSource(message),
                                                gitCommitId);
                                        transaction.commit();
                                    }
                                }
                            } else if (eventConsumers.isUnsatisfied()) {
                                // TODO log
                            } else if (eventConsumers.isAmbiguous()) {
                                throw new IllegalStateException("Ambiguous command handlers for " + aggregateRootType + " " + eventType);
                            }
                            eventConsumedRepository.markEventAsConsumed(eventId, new Date(), new ConsumerRecordKafkaSource(message));
                        } else {
                            LOGGER.log(Level.INFO, String.format("Event '%s' already consumed", eventId));
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unknown type " + debeziumEventKafkaMessage.encryptedEventType());
                }
            } catch (final NotSupportedException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException e) {
                throw new RuntimeException(e);
            } catch (final UnableToDecodeDebeziumEventMessageException unableToDecodeDebeziumEventMessageException) {
                LOGGER.log(Level.WARNING, String.format("Unable to decode debezium event message in topic '%s' in partition '%d' in offset '%d' get message '%s'",
                        unableToDecodeDebeziumEventMessageException.topic(),
                        unableToDecodeDebeziumEventMessageException.partition(),
                        unableToDecodeDebeziumEventMessageException.offset(),
                        unableToDecodeDebeziumEventMessageException.getMessage()));
            }
            return null;
        }, executor);
    }

    private class EventQualifierLiteral extends AnnotationLiteral<EventQualifier> implements EventQualifier {

        private final String aggregateRootType;
        private final String eventType;

        private EventQualifierLiteral(final String aggregateRootType,
                                      final String eventType) {
            this.aggregateRootType = aggregateRootType;
            this.eventType = eventType;
        }

        @Override
        public String aggregateRootType() {
            return aggregateRootType;
        }

        @Override
        public String eventType() {
            return eventType;
        }

    }

}
