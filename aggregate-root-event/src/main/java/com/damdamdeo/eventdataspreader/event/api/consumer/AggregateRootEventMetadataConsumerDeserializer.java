package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface AggregateRootEventMetadataConsumerDeserializer {

    AggregateRootEventMetadataConsumer deserialize(Optional<EncryptedEventSecret> encryptedEventSecret, String eventConsumerMetadata);

}
