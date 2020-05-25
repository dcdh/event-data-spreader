package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent;

import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftOfferedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayloadDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonGiftAggregateRootGiftOfferedAggregateRootEventPayloadTest {

    @Inject
    JacksonAggregateRootEventPayloadDeSerializer jacksonAggregateRootEventPayloadDeSerializer;

    @Test
    public void should_serialize() {
        // Given

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(Optional.empty(),
                new GiftAggregateRootGiftOfferedAggregateRootEventPayload("name", "offeredTo"));

        // Then
        assertEquals("{\"@type\":\"GiftAggregateRootGiftOfferedAggregateRootEventPayload\",\"name\":\"name\",\"offeredTo\":\"offeredTo\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayload deserialized = jacksonAggregateRootEventPayloadDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"GiftAggregateRootGiftOfferedAggregateRootEventPayload\",\"name\":\"name\",\"offeredTo\":\"offeredTo\"}");

        // Then
        assertEquals(new GiftAggregateRootGiftOfferedAggregateRootEventPayload("name", "offeredTo"), deserialized);
    }

}
