package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AccountAggregateAccountDebitedAggregateRootEventPayloadConsumerTest {

    @Inject
    JacksonAggregateRootEventPayloadConsumerDeserializer jacksonAggregateRootEventPayloadConsumerDeserializer;

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"AccountAggregateAccountDebitedAggregateRootEventPayload\",\"owner\":\"damdamdeo\",\"balance\":10}");

        // Then
        assertEquals(new AccountAggregateAccountDebitedAggregateRootEventPayloadConsumer("damdamdeo", BigDecimal.TEN), deserialized);
    }
}
