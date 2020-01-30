package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

public interface AggregateRootEventPayloadDeSerializer {

    String serialize(EncryptedEventSecret encryptedEventSecret, AggregateRootEventPayload aggregateRootEventPayload);

    AggregateRootEventPayload deserialize(EncryptedEventSecret encryptedEventSecret, String eventPayload);

}