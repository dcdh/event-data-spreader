package com.damdamdeo.eventdataspreader.writeside.debeziumeventconsumer.api;

import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.util.UUID;

public interface Event {

    UUID eventId();

    String aggregateRootId();

    String aggregateRootType();

    Date creationDate();

    String eventType();

    JsonObject metadata();

    JsonObject payload();

    Long version();

}
