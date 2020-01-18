package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public interface EventPayloadIdentifier {

    String aggregateRootType();

    String eventType();

    String eventPayloadType();

    String aggregateRootId();

}
