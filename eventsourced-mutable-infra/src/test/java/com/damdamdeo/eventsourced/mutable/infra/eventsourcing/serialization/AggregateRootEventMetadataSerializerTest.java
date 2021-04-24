package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootEventMetadataSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata.MetadataEnhancerContextHolder;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AggregateRootEventMetadataSerializerTest {

    @Inject
    AggregateRootEventMetadataSerializer aggregateRootEventMetadataSerializer;

    @Test
    public void should_return_empty_object_when_serialize() {
        // Given
        MetadataEnhancerContextHolder.put("user.anonymous", false);
        MetadataEnhancerContextHolder.put("user.name", "damdamdeo");

        // When
        final String serialized = aggregateRootEventMetadataSerializer.serialize();

        // Then
        assertEquals("{\"user.anonymous\":false, \"user.name\":\"damdamdeo\"}", serialized);

        MetadataEnhancerContextHolder.cleanupThread();
    }

}
