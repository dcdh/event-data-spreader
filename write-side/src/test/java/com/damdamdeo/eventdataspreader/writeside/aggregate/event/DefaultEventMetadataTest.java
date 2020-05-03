package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.JacksonEventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.JacksonEventMetadataSerializer;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultEventMetadataTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DefaultEventMetadata.class).verify();
    }

    private static class DefaultJacksonEventMetadataSubtypes implements JacksonEventMetadataSubtypes {

        @Override
        public List<JacksonSubtype<EventMetadata>> jacksonSubtypes() {
            return Collections.singletonList(new JacksonSubtype<>(DefaultEventMetadata.class, "DefaultEventMetadata"));
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final EventMetadataSerializer eventMetadataSerializer = new JacksonEventMetadataSerializer(new DefaultJacksonEventMetadataSubtypes());

        // When
        final String serialized = eventMetadataSerializer.serialize(Optional.empty(),
                new DefaultEventMetadata("executedBy"));

        // Then
        assertEquals("{\"@type\":\"DefaultEventMetadata\",\"executedBy\":\"executedBy\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given
        final EventMetadataDeserializer eventMetadataDeserializer = new JacksonEventMetadataDeserializer(new DefaultJacksonEventMetadataSubtypes());

        // When
        final EventMetadata deserialized = eventMetadataDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"DefaultEventMetadata\",\"executedBy\":\"executedBy\"}");

        // Then
        assertEquals(new DefaultEventMetadata("executedBy"), deserialized);
    }

}
