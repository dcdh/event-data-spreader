package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.event.api.EventPayload;
import com.damdamdeo.eventdataspreader.event.api.EventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.JacksonEventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonAccountAggregateAccountDebitedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JacksonAccountAggregateAccountDebitedEventPayload.class).verify();
    }

    private static class DefaultJacksonEventPayloadSubtypes implements JacksonEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(JacksonAccountAggregateAccountDebitedEventPayload.class, "AccountAggregateAccountDebitedEventPayload"));
        }

    }

    @Test
    public void should_deserialize() {
        // Given
        final EventPayloadDeserializer eventPayloadDeserializer = new JacksonEventPayloadDeserializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final EventPayload deserialized = eventPayloadDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"AccountAggregateAccountDebitedEventPayload\",\"owner\":\"damdamdeo\",\"balance\":10}");

        // Then
        assertEquals(new JacksonAccountAggregateAccountDebitedEventPayload("damdamdeo", BigDecimal.TEN), deserialized);
    }

}
