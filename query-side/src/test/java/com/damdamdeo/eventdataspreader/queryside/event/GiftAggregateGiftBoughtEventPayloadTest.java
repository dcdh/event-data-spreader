package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.JacksonEventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled// FIXME Fail to build using maven !!!
public class GiftAggregateGiftBoughtEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(GiftAggregateGiftBoughtEventPayload.class).verify();
    }

    private static class DefaultJacksonEventPayloadSubtypes implements JacksonEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(GiftAggregateGiftBoughtEventPayload.class, "GiftAggregateGiftBoughtEventPayload"));
        }

    }

    private static class DefaultEncryptedEventSecret implements EncryptedEventSecret {

        @Override
        public String aggregateRootId() {
            return null;
        }

        @Override
        public String aggregateRootType() {
            return null;
        }

        @Override
        public Date creationDate() {
            return null;
        }

        @Override
        public String secret() {
            return null;
        }

    }

    @Test
    public void should_deserialize() {
        // Given
        final EventPayloadDeserializer eventPayloadDeserializer = new JacksonEventPayloadDeserializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final EventPayload deserialized = eventPayloadDeserializer.deserialize(new DefaultEncryptedEventSecret(),
                "{\"@type\":\"GiftAggregateGiftBoughtEventPayload\",\"name\":\"damdamdeo\"}");

        // Then
        assertEquals(new GiftAggregateGiftBoughtEventPayload("damdamdeo"), deserialized);
    }

}
