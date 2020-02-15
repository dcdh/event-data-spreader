package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface EventPayloadDeserializer {

    EventPayload deserialize(Optional<EncryptedEventSecret> encryptedEventSecret, String eventPayload);

}
