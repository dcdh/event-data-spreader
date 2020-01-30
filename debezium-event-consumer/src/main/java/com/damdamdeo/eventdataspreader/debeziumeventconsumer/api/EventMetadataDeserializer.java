package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

public interface EventMetadataDeserializer {

    EventMetadata deserialize(EncryptedEventSecret encryptedEventSecret, String eventMetadata);

}
