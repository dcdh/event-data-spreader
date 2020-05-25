package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent;

import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayloadDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayloadTest {

    @Inject
    JacksonAggregateRootEventPayloadDeSerializer jacksonAggregateRootEventPayloadDeSerializer;

    @Test
    public void should_serialize() {
        // Given

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(Optional.empty(),
                new GiftAggregateRootGiftBoughtAggregateRootEventPayload("name"));

        // Then
        assertEquals("{\"@type\":\"GiftAggregateRootGiftBoughtAggregateRootEventPayload\",\"name\":\"name\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayload deserialized = jacksonAggregateRootEventPayloadDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"GiftAggregateRootGiftBoughtAggregateRootEventPayload\",\"name\":\"name\"}");

        // Then
        assertEquals(new GiftAggregateRootGiftBoughtAggregateRootEventPayload("name"), deserialized);
    }

}
