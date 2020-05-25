package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent;

import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.AccountAggregateRootAccountDebitedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayloadDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonAccountAggregateRootAccountDebitedAggregateRootEventPayloadTest {

    @Inject
    JacksonAggregateRootEventPayloadDeSerializer jacksonAggregateRootEventPayloadDeSerializer;

    @Test
    public void should_serialize() {
        // Given

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(Optional.empty(),
                new AccountAggregateRootAccountDebitedAggregateRootEventPayload("owner", new BigDecimal("100.01"), new BigDecimal("899.99")));

        // Then
        assertEquals("{\"@type\":\"AccountAggregateRootAccountDebitedAggregateRootEventPayload\",\"owner\":\"owner\",\"price\":100.01,\"balance\":899.99}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayload deserialized = jacksonAggregateRootEventPayloadDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"AccountAggregateRootAccountDebitedAggregateRootEventPayload\",\"owner\":\"owner\",\"price\":100.01,\"balance\":899.99}");

        // Then
        assertEquals(new AccountAggregateRootAccountDebitedAggregateRootEventPayload("owner", new BigDecimal("100.01"), new BigDecimal("899.99")), deserialized);
    }

}
