package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftBought;
import com.damdamdeo.eventdataspreader.writeside.user.type.GiftEventPayloadsAdapter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GiftBoughtTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new GiftEventPayloadsAdapter()));

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(GiftBought.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final GiftBought giftBought = new GiftBought("name");

        // When
        final String json = MAPPER.toJson(giftBought);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"GiftBought\", \"name\": \"name\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"GiftBought\", \"name\": \"name\"}";

        // When
        final GiftBought giftBought = (GiftBought) MAPPER.fromJson(json, GiftBought.class);

        // Then
        assertEquals("name", giftBought.name());
    }

}
