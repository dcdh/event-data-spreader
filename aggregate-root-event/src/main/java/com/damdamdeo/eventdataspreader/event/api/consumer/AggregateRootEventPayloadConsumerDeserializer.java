package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface AggregateRootEventPayloadConsumerDeserializer {

    AggregateRootEventPayloadConsumer deserialize(Optional<EncryptedEventSecret> encryptedEventSecret, String eventPayload);

}
