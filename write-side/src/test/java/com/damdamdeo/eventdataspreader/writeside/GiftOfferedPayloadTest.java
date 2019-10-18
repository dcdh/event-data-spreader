package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftOfferedPayload;
import com.damdamdeo.eventdataspreader.writeside.user.type.DefaultEventPayloadsAdapter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GiftOfferedPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultEventPayloadsAdapter()));

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(GiftOfferedPayload.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final GiftOfferedPayload giftOfferedPayload = new GiftOfferedPayload("name", "offeredTo");

        // When
        final String json = MAPPER.toJson(giftOfferedPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@payloadType\": \"GiftOfferedPayload\", \"@aggregaterootType\": \"GiftAggregate\", \"name\": \"name\", \"offeredTo\": \"offeredTo\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@payloadType\": \"GiftOfferedPayload\", \"@aggregaterootType\": \"GiftAggregate\", \"name\": \"name\", \"offeredTo\": \"offeredTo\"}";

        // When
        final GiftOfferedPayload giftOfferedPayload = (GiftOfferedPayload) MAPPER.fromJson(json, GiftOfferedPayload.class);

        // Then
        assertEquals("name", giftOfferedPayload.name());
        assertEquals("offeredTo", giftOfferedPayload.offeredTo());
    }

}
