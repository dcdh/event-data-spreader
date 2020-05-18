package com.damdamdeo.eventdataspreader.event.api;

import java.time.LocalDateTime;

public interface Event {

    EventId eventId();

    String eventType();

    LocalDateTime creationDate();

    EventPayload eventPayload();

    EventMetadata eventMetaData();

}
