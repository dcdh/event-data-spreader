package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Secret;

public interface AggregateRootEventMetadataConsumerDeserializer {

    AggregateRootEventMetadataConsumer deserialize(Secret secret, String eventConsumerMetadata);

}
