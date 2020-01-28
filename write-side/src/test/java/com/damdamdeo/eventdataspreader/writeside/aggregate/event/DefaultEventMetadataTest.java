package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.JacksonEventMetadataSerializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled// FIXME Fail to build using maven !!!
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

    private static class DefaultEncryptedEventSecret implements EncryptedEventSecret {

        @Override
        public String aggregateRootId() {
            return null;
        }

        @Override
        public String aggregateRootType() {
            return null;
        }

        @Override
        public Date creationDate() {
            return null;
        }

        @Override
        public String secret() {
            return null;
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final EventMetadataSerializer eventMetadataSerializer = new JacksonEventMetadataSerializer(new DefaultJacksonEventMetadataSubtypes());

        // When
        final String serialized = eventMetadataSerializer.serialize(new DefaultEncryptedEventSecret(),
                new DefaultEventMetadata("executedBy"));

        // Then
        assertEquals("{\"@type\":\"DefaultEventMetadata\",\"executedBy\":\"executedBy\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given
        final EventMetadataSerializer eventMetadataSerializer = new JacksonEventMetadataSerializer(new DefaultJacksonEventMetadataSubtypes());

        // When
        final EventMetadata deserialized = eventMetadataSerializer.deserialize(new DefaultEncryptedEventSecret(),
                "{\"@type\":\"DefaultEventMetadata\",\"executedBy\":\"executedBy\"}");

        // Then
        assertEquals(new DefaultEventMetadata("executedBy"), deserialized);
    }

}
