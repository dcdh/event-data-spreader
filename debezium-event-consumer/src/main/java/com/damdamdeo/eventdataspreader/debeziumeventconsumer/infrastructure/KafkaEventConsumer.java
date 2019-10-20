package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class KafkaEventConsumer {

    private final static Logger LOGGER = Logger.getLogger(KafkaEventConsumer.class.getName());

    private static final class NotAnEventPayloadException extends RuntimeException {}

    @Inject
    EventConsumedRepository eventConsumedRepository;

    @Inject
    UserTransaction transaction;

    private final class DefaultEvent implements Event {

        private final UUID eventId;
        private final String aggregateRootId;
        private final String aggregateRootType;
        private final Date creationDate;
        private final String eventType;
        private final JsonObject metadata;
        private final JsonObject payload;
        private final Long version;

        public DefaultEvent(final JsonObject after) {
            this.eventId = UUID.fromString(after.getString("eventid"));
            this.aggregateRootId = after.getString("aggregaterootid");
            this.aggregateRootType = after.getString("aggregateroottype");
            this.creationDate = new Date(after.getLong("creationdate") / 1000);
            this.eventType = after.getString("eventtype");
            this.metadata = new JsonObject(after.getString("metadata"));
            this.payload = new JsonObject(after.getString("eventpayload"));
            this.version = after.getLong("version");
        }

        @Override
        public UUID eventId() {
            return eventId;
        }

        @Override
        public String aggregateRootId() {
            return aggregateRootId;
        }

        @Override
        public String aggregateRootType() {
            return aggregateRootType;
        }

        @Override
        public Date creationDate() {
            return creationDate;
        }

        @Override
        public String eventType() {
            return eventType;
        }

        @Override
        public JsonObject metadata() {
            return metadata;
        }

        @Override
        public JsonObject payload() {
            return payload;
        }

        @Override
        public Long version() {
            return version;
        }

    }

    @Incoming("event")
    public CompletionStage<Void> onMessage(final KafkaMessage<JsonObject, JsonObject> message) throws Exception {
        if (message.getPayload() == null) {
            return message.ack();
        }
        try {
            final UUID eventId = Optional.ofNullable(message.getKey().getJsonObject("payload"))
                    .map(jsonObject -> jsonObject.getString("eventid"))
                    .map(UUID::fromString)
                    .orElseThrow(() -> new NotAnEventPayloadException());
            if (!eventConsumedRepository.hasConsumedEvent(eventId)) {
                final JsonObject payload = message.getPayload().getJsonObject("payload");
                if (payload != null) {
                    final JsonObject after = payload.getJsonObject("after");
                    if (after != null) {
                        executeEventMessage(new DefaultEvent(after));
                    } else {
                        LOGGER.log(Level.INFO, String.format("Missing 'after' for eventId '%s'", eventId));
                    }
                } else {
                    LOGGER.log(Level.INFO, String.format("Missing 'payload' for eventId '%s'", eventId));
                }
            } else {
                LOGGER.log(Level.INFO, String.format("Event '%s' already consumed", eventId));
            }
        } catch (final NotAnEventPayloadException notAnEventPayloadException) {
            LOGGER.log(Level.WARNING, String.format("Message not an event !"));// TODO better login
        }
        return message.ack();
    }

    // je creer des commandes à partir d'un event ;)
    private void executeEventMessage(final Event event) throws Exception {
        final Instance<EventConsumer> eventConsumers = CDI.current()
                .select(EventConsumer.class, new EventQualifierLiteral(event));
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
//            TODO log
        } else if (eventConsumers.isAmbiguous()) {
            throw new IllegalStateException("Ambigous command handlers for " + event.aggregateRootType() + " " + event.eventType());
        }
        transaction.begin();
        eventConsumedRepository.markEventAsConsumed(event.eventId(), new Date());
        transaction.commit();
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
