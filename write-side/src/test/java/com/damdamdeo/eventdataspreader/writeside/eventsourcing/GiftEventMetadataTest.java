package com.damdamdeo.eventdataspreader.writeside.eventsourcing;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.event.GiftEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.user.type.GiftEventMetadataAdapter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GiftEventMetadataTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new GiftEventMetadataAdapter()));

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(GiftEventMetadata.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final GiftEventMetadata giftEventMetadata = new GiftEventMetadata("executedBy");

        // When
        final String json = MAPPER.toJson(giftEventMetadata);

        // Then
        JSONAssert.assertEquals(
                "{\"executedBy\": \"executedBy\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"executedBy\": \"executedBy\"}";

        // When
        final GiftEventMetadata giftEventMetadata = (GiftEventMetadata) MAPPER.fromJson(json, GiftEventMetadata.class);

        // Then
        assertEquals("executedBy", giftEventMetadata.executedBy());
    }

}
