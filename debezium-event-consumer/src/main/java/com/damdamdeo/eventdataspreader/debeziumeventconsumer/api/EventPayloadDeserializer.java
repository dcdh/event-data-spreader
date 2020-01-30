package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

public interface EventPayloadDeserializer {

    EventPayload deserialize(EncryptedEventSecret encryptedEventSecret, String eventPayload);

}
