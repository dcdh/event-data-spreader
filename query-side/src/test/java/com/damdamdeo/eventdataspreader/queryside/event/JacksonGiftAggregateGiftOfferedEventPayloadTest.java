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

public class JacksonGiftAggregateGiftOfferedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JacksonGiftAggregateGiftOfferedEventPayload.class).verify();
    }

    private static class DefaultJacksonEventPayloadSubtypes implements JacksonEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(JacksonGiftAggregateGiftOfferedEventPayload.class, "GiftAggregateGiftOfferedEventPayload"));
        }

    }

    @Test
    public void should_deserialize() {
        // Given
        final EventPayloadDeserializer eventPayloadDeserializer = new JacksonEventPayloadDeserializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final EventPayload deserialized = eventPayloadDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"GiftAggregateGiftOfferedEventPayload\",\"name\":\"MotorolaG6\",\"offeredTo\":\"damdamdeo\"}");

        // Then
        assertEquals(new JacksonGiftAggregateGiftOfferedEventPayload("MotorolaG6", "damdamdeo"), deserialized);
    }

}
