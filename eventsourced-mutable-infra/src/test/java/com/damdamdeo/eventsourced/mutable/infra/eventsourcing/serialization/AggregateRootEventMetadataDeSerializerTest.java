package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata.MetadataEnhancerContextHolder;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AggregateRootEventMetadataDeSerializerTest {

    @Inject
    AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer;

    @Test
    public void should_return_empty_object_when_serialize() {
        // Given
        MetadataEnhancerContextHolder.put("user.anonymous", false);
        MetadataEnhancerContextHolder.put("user.name", "damdamdeo");

        // When
        final String serialized = aggregateRootEventMetadataDeSerializer.serialize();

        // Then
        assertEquals("{\"user.anonymous\":false,\"user.name\":\"damdamdeo\"}", serialized);

        MetadataEnhancerContextHolder.cleanupThread();
    }

}
