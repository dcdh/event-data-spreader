package com.damdamdeo.eventdataspreader.queryside.infrastructure.consumer.aggregateevent.payload;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventPayloadConsumerDeserializer;
import com.damdamdeo.eventdataspreader.queryside.consumer.payload.GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonGiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumerTest {

    @Inject
    JacksonAggregateRootEventPayloadConsumerDeserializer jacksonAggregateRootEventPayloadConsumerDeserializer;

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"GiftAggregateRootGiftOfferedAggregateRootEventPayload\",\"name\":\"lapinou\",\"offeredTo\":\"damdamdeo\"}");

        // Then
        assertEquals(new GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer("lapinou", "damdamdeo"), deserialized);
    }

}
