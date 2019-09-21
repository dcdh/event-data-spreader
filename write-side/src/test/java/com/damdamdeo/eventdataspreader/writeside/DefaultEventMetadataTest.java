package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.user.type.DefaultEventMetadataAdapter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultEventMetadataTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultEventMetadataAdapter()));

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(DefaultEventMetadata.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final DefaultEventMetadata defaultEventMetadata = new DefaultEventMetadata("executedBy");

        // When
        final String json = MAPPER.toJson(defaultEventMetadata);

        // Then
        JSONAssert.assertEquals(
                "{\"executedBy\": \"executedBy\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"executedBy\": \"executedBy\"}";

        // When
        final DefaultEventMetadata defaultEventMetadata = (DefaultEventMetadata) MAPPER.fromJson(json, DefaultEventMetadata.class);

        // Then
        assertEquals("executedBy", defaultEventMetadata.executedBy());
    }

}
