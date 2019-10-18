package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftBoughtPayload;
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

public class GiftBoughtPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultEventPayloadsAdapter()));

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(GiftBoughtPayload.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final GiftBoughtPayload giftBoughtPayload = new GiftBoughtPayload("name");

        // When
        final String json = MAPPER.toJson(giftBoughtPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@payloadType\": \"GiftBoughtPayload\", \"@aggregaterootType\": \"GiftAggregate\", \"name\": \"name\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@payloadType\": \"GiftBoughtPayload\", \"@aggregaterootType\": \"GiftAggregate\", \"name\": \"name\"}";

        // When
        final GiftBoughtPayload giftBoughtPayload = (GiftBoughtPayload) MAPPER.fromJson(json, GiftBoughtPayload.class);

        // Then
        assertEquals("name", giftBoughtPayload.name());
    }

}
