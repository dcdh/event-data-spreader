package com.damdamdeo.eventdataspreader.writeside.eventsourcing;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.event.GiftOffered;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.user.type.GiftEventPayloadsAdapter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GiftOfferedTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new GiftEventPayloadsAdapter()));


    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(GiftOffered.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final GiftOffered giftOffered = new GiftOffered("name", "offeredTo");

        // When
        final String json = MAPPER.toJson(giftOffered);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"GiftOffered\", \"name\": \"name\", \"offeredTo\": \"offeredTo\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"GiftOffered\", \"name\": \"name\", \"offeredTo\": \"offeredTo\"}";

        // When
        final GiftOffered giftOffered = (GiftOffered) MAPPER.fromJson(json, GiftOffered.class);

        // Then
        assertEquals("name", giftOffered.name());
        assertEquals("offeredTo", giftOffered.offeredTo());
    }

}
