package com.damdamdeo.eventdataspreader.writeside.eventsourcing;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.user.type.GiftAggregateRootAdapter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GiftAggregateTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new GiftAggregateRootAdapter()));

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final GiftAggregate giftAggregate = new GiftAggregate("aggregateRootId",
                "name",
                "offeredTo",
                1L);

        // When
        final String json = MAPPER.toJson(giftAggregate);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"GiftAggregate\", \"aggregateRootId\": \"aggregateRootId\", \"name\": \"name\", \"offeredTo\": \"offeredTo\", \"version\": 1}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"GiftAggregate\", \"aggregateRootId\": \"aggregateRootId\", \"name\": \"name\", \"offeredTo\": \"offeredTo\", \"version\": 1}";

        // When
        final GiftAggregate giftAggregate = (GiftAggregate) MAPPER.fromJson(json, GiftAggregate.class);

        // Then
        assertEquals("aggregateRootId", giftAggregate.aggregateRootId());
        assertEquals("name", giftAggregate.name());
        assertEquals("offeredTo", giftAggregate.offeredTo());
        assertEquals(1l, giftAggregate.version());
    }

}
