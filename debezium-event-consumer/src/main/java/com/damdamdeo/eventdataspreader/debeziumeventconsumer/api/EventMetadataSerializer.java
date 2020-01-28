package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

public interface EventMetadataSerializer {

    String serialize(EncryptedEventSecret encryptedEventSecret, EventMetadata eventMetadata);

    EventMetadata deserialize(EncryptedEventSecret encryptedEventSecret, String eventMetadata);

}
