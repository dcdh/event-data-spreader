package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

public interface EventPayloadSerializer {

    String serialize(EncryptedEventSecret encryptedEventSecret, EventPayload aggregateRootEventPayload);

    EventPayload deserialize(EncryptedEventSecret encryptedEventSecret, String eventPayload);

}
