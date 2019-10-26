package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.ReceivedKafkaMessage;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.transaction.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class KafkaEventConsumer {

    private final static Logger LOGGER = Logger.getLogger(KafkaEventConsumer.class.getName());

    @Inject
    EventConsumedRepository eventConsumedRepository;

    @Inject
    UserTransaction transaction;

    @Inject
    @Any
    Instance<EventConsumer> eventConsumersBeans;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Incoming("event")
    public CompletionStage<Void> onMessage(final KafkaMessage<JsonObject, JsonObject> message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final Event event = new DebeziumEventKafkaMessage(message);
                final UUID eventId = event.eventId();
                if (!eventConsumedRepository.hasConsumedEvent(eventId)) {
                    final Instance<EventConsumer> eventConsumers = eventConsumersBeans.select(EventConsumer.class, new EventQualifierLiteral(event));
                    if (eventConsumers.isResolvable()) {
                        transaction.begin();
                        final List<String> consumedEventClassNames = eventConsumedRepository.getConsumedEventsForEventId(event.eventId());
                        transaction.commit();
                        for (final EventConsumer eventConsumer : eventConsumers) {
                            if (!consumedEventClassNames.contains(eventConsumer.getClass().getName())) {
                                transaction.begin();
                                eventConsumer.consume(event);
                                eventConsumedRepository.addEventConsumerConsumed(event.eventId(), eventConsumer.getClass());
                                transaction.commit();
                            }
                        }
                    } else if (eventConsumers.isUnsatisfied()) {
                        // TODO log
                    } else if (eventConsumers.isAmbiguous()) {
                        throw new IllegalStateException("Ambiguous command handlers for " + event.aggregateRootType() + " " + event.eventType());
                    }
                    transaction.begin();
                    eventConsumedRepository.markEventAsConsumed(event.eventId(), new Date());
                    transaction.commit();
                } else {
                    LOGGER.log(Level.INFO, String.format("Event '%s' already consumed", eventId));
                }
                return null;
            } catch (final NotSupportedException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException e) {
                throw new RuntimeException(e);
            } catch (final UnableToDecodeDebeziumEventMessageException e) {
                final ConsumerRecord<JsonObject, JsonObject> consumerRecord = ((ReceivedKafkaMessage<JsonObject, JsonObject>) message).unwrap();
                final Long offset = consumerRecord.offset();
                final Integer partition = consumerRecord.partition();
                final String topic = consumerRecord.topic();
                LOGGER.log(Level.WARNING, String.format("Unable to decode debezium event message in topic '%s' in partition '%d' in offset '%d'",
                        topic, partition, offset));
            }
            return null;
        }, executor);
    }

    private class EventQualifierLiteral extends AnnotationLiteral<EventQualifier> implements EventQualifier {

        private final String aggregateRootType;
        private final String eventType;

        private EventQualifierLiteral(final Event event) {
            this.aggregateRootType = event.aggregateRootType();
            this.eventType = event.eventType();
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
