package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.metadata;

import com.damdamdeo.eventdataspreader.writeside.aggregate.metadata.UserAggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventMetadataDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonUserAggregateRootEventMetadataTest {

    @Inject
    JacksonAggregateRootEventMetadataDeSerializer jacksonAggregateRootEventMetadataDeSerializer;

    @Test
    public void should_serialize() {
        // Given

        // When
        final String serialized = jacksonAggregateRootEventMetadataDeSerializer.serialize(Optional.empty(),
                new UserAggregateRootEventMetadata("damdamdeo"));

        // Then
        assertEquals("{\"@type\":\"UserAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}", serialized);
    }
    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventMetadata deserialized = jacksonAggregateRootEventMetadataDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"UserAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}");

        // Then
        assertEquals(new UserAggregateRootEventMetadata("damdamdeo"), deserialized);
    }

}
