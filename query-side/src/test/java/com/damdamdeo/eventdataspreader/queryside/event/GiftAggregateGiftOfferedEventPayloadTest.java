package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayloadSerializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.JacksonEventPayloadSerializer;
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
public class GiftAggregateGiftOfferedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(GiftAggregateGiftOfferedEventPayload.class).verify();
    }

    private static class DefaultJacksonEventPayloadSubtypes implements JacksonEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(GiftAggregateGiftOfferedEventPayload.class, "GiftAggregateGiftOfferedEventPayload"));
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
    public void should_serialized() {
        // Given
        final EventPayloadSerializer eventPayloadSerializer = new JacksonEventPayloadSerializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final String serialized = eventPayloadSerializer.serialize(new DefaultEncryptedEventSecret(),
                new GiftAggregateGiftOfferedEventPayload("MotorolaG6", "damdamdeo"));

        // Then
        assertEquals("{\"@type\":\"GiftAggregateGiftOfferedEventPayload\",\"name\":\"MotorolaG6\",\"offeredTo\":\"damdamdeo\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given
        final EventPayloadSerializer eventPayloadSerializer = new JacksonEventPayloadSerializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final EventPayload deserialized = eventPayloadSerializer.deserialize(new DefaultEncryptedEventSecret(),
                "{\"@type\":\"GiftAggregateGiftOfferedEventPayload\",\"name\":\"MotorolaG6\",\"offeredTo\":\"damdamdeo\"}");

        // Then
        assertEquals(new GiftAggregateGiftOfferedEventPayload("MotorolaG6", "damdamdeo"), deserialized);
    }

}
