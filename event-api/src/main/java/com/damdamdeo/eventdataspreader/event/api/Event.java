package com.damdamdeo.eventdataspreader.event.api;

import java.time.LocalDateTime;

public interface Event {

    EventId eventId();

    String aggregateRootId();

    String aggregateRootType();

    String eventType();

    Long version();

    LocalDateTime creationDate();

    EventPayload eventPayload();

    EventMetadata eventMetaData();

}
