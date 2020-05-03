package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.event.api.EventPayload;
import com.damdamdeo.eventdataspreader.event.api.EventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.JacksonEventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void should_deserialize() {
        // Given
        final EventPayloadDeserializer eventPayloadDeserializer = new JacksonEventPayloadDeserializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final EventPayload deserialized = eventPayloadDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"GiftAggregateGiftBoughtEventPayload\",\"name\":\"damdamdeo\"}");

        // Then
        assertEquals(new GiftAggregateGiftBoughtEventPayload("damdamdeo"), deserialized);
    }

}
