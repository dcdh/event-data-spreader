package com.damdamdeo.eventdataspreader.writeside.infrastructure.consumer.aggregateevent.payload;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventPayloadConsumerDeserializer;
import com.damdamdeo.eventdataspreader.writeside.consumer.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumerTest {

    @Inject
    JacksonAggregateRootEventPayloadConsumerDeserializer jacksonAggregateRootEventPayloadConsumerDeserializer;

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"GiftAggregateRootGiftBoughtAggregateRootEventPayload\",\"name\":\"damdamdeo\"}");

        // Then
        assertEquals(new GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer("damdamdeo"), deserialized);
    }
}
