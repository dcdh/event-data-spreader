package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.JacksonEventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled// FIXME Fail to build using maven !!!
public class AccountAggregateAccountDebitedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AccountAggregateAccountDebitedEventPayload.class).verify();
    }

    private static class DefaultJacksonEventPayloadSubtypes implements JacksonEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(AccountAggregateAccountDebitedEventPayload.class, "AccountAggregateAccountDebitedEventPayload"));
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
        assertEquals(new AccountAggregateAccountDebitedEventPayload("damdamdeo", BigDecimal.TEN), deserialized);
    }

}
