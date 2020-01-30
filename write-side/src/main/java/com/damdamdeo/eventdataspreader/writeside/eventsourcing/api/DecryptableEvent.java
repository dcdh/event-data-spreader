package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Date;

public interface DecryptableEvent {

    String eventId();

    String aggregateRootId();

    String aggregateRootType();

    String eventType();

    Long version();

    Date creationDate();

    AggregateRootEventPayload eventPayload(EncryptedEventSecret encryptedEventSecret, AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer);

    EventMetadata eventMetaData(EncryptedEventSecret encryptedEventSecret, EventMetadataDeserializer eventMetadataDeserializer);

}