package com.damdamdeo.eventdataspreader.writeside.infrastructure.consumer.aggregateevent.metadata;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventMetadataConsumerDeserializer;
import com.damdamdeo.eventdataspreader.writeside.consumer.metadata.UserAggregateRootEventMetadataConsumer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonUserAggregateRootEventMetadataConsumerTest {

    @Inject
    JacksonAggregateRootEventMetadataConsumerDeserializer jacksonAggregateRootEventMetadataConsumerDeserializer;

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventMetadataConsumer deserialized = jacksonAggregateRootEventMetadataConsumerDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"UserAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}");

        // Then
        assertEquals(new UserAggregateRootEventMetadataConsumer("damdamdeo"), deserialized);
    }

}
