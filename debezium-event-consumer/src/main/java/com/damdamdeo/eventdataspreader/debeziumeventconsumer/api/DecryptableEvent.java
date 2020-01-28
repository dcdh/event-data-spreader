package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Date;

public interface DecryptableEvent {

    String eventId();

    String aggregateRootId();

    String aggregateRootType();

    String eventType();

    Long version();

    Date creationDate();

    EventPayload eventPayload(EncryptedEventSecret encryptedEventSecret, EventPayloadSerializer eventPayloadSerializer);

    EventMetadata eventMetaData(EncryptedEventSecret encryptedEventSecret, EventMetadataSerializer eventMetadataSerializer);

}
