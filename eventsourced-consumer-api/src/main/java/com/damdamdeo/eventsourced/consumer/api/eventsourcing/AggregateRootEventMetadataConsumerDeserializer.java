package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.util.Optional;

public interface AggregateRootEventMetadataConsumerDeserializer {

    AggregateRootEventMetadataConsumer deserialize(Optional<AggregateRootSecret> aggregateRootSecret, String eventConsumerMetadata);

}
