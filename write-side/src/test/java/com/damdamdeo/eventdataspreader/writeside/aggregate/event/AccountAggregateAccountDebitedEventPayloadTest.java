package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventPayloadSubtypes;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountAggregateAccountDebitedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AccountAggregateAccountDebitedEventPayload.class).verify();
    }

    private static class DefaultJacksonAggregateRootEventPayloadSubtypes implements JacksonAggregateRootEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<AggregateRootEventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(AccountAggregateAccountDebitedEventPayload.class, "AccountAggregateAccountDebitedEventPayload"));
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer = new JacksonAggregateRootEventPayloadDeSerializer(new DefaultJacksonAggregateRootEventPayloadSubtypes());

        // When
        final String serialized = aggregateRootEventPayloadDeSerializer.serialize(Optional.empty(),
                new AccountAggregateAccountDebitedEventPayload("owner", new BigDecimal("100.01"), new BigDecimal("899.99")));

        // Then
        assertEquals("{\"@type\":\"AccountAggregateAccountDebitedEventPayload\",\"owner\":\"owner\",\"price\":100.01,\"balance\":899.99}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer = new JacksonAggregateRootEventPayloadDeSerializer(new DefaultJacksonAggregateRootEventPayloadSubtypes());

        // When
        final AggregateRootEventPayload deserialized = aggregateRootEventPayloadDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"AccountAggregateAccountDebitedEventPayload\",\"owner\":\"owner\",\"price\":100.01,\"balance\":899.99}");

        // Then
        assertEquals(new AccountAggregateAccountDebitedEventPayload("owner", new BigDecimal("100.01"), new BigDecimal("899.99")), deserialized);
    }

}
