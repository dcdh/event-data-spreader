package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface EventMetadataSerializer {

    String serialize(Optional<EncryptedEventSecret> encryptedEventSecret, EventMetadata eventMetadata);

}
