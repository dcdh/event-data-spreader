package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbAggregateRootEventId;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbAggregateRootEvent;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public final class DebeziumJsonbAggregateRootEventConsumable implements AggregateRootEventConsumable<JsonObject> {

    private final AggregateRootEventId eventId;

    private final String eventType;

    private final LocalDateTime creationDate;

    private final JsonObject eventPayload;

    private final JsonObject eventMetaData;

    private final JsonObject materializedState;

    public DebeziumJsonbAggregateRootEventConsumable(final DebeziumJsonbAggregateRootEvent debeziumJsonbAggregateRootEvent) {
        Objects.requireNonNull(debeziumJsonbAggregateRootEvent);
        this.eventId = new DebeziumJsonbAggregateRootEventId(debeziumJsonbAggregateRootEvent);
        this.eventType = debeziumJsonbAggregateRootEvent.eventType();
        final Instant instant = Instant.ofEpochMilli(debeziumJsonbAggregateRootEvent.creationDate() / 1000);
        this.creationDate = instant.atZone(ZoneOffset.UTC).toLocalDateTime();
        this.eventPayload = Json.createReader(new StringReader(debeziumJsonbAggregateRootEvent.eventPayload())).readObject();
        this.eventMetaData = Json.createReader(new StringReader(debeziumJsonbAggregateRootEvent.eventMetadata())).readObject();
        this.materializedState = Json.createReader(new StringReader(debeziumJsonbAggregateRootEvent.materializedState())).readObject();
    }

    @Override
    public AggregateRootEventId eventId() {
        return eventId;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public LocalDateTime creationDate() {
        return creationDate;
    }

    @Override
    public JsonObject eventPayload() {
        return eventPayload;
    }

    @Override
    public JsonObject eventMetaData() {
        return eventMetaData;
    }

    @Override
    public JsonObject materializedState() {
        return materializedState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumJsonbAggregateRootEventConsumable)) return false;
        DebeziumJsonbAggregateRootEventConsumable that = (DebeziumJsonbAggregateRootEventConsumable) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(eventPayload, that.eventPayload) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(materializedState, that.materializedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, creationDate, eventPayload, eventMetaData, materializedState);
    }
}
