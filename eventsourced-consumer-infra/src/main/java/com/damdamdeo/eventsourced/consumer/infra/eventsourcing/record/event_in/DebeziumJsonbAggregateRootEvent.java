package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class DebeziumJsonbAggregateRootEvent {

    private final String aggregateRootType;

    private final String aggregateRootId;

    private final Long version;

    private final Long creationDate;

    private final String eventType;

    private final String eventMetadata;

    private final String eventPayload;

    private final String materializedState;

    @JsonbCreator
    public DebeziumJsonbAggregateRootEvent(@JsonbProperty("aggregateroottype") final String aggregateRootType,
                                           @JsonbProperty("aggregaterootid") final String aggregateRootId,
                                           @JsonbProperty("version") final Long version,
                                           @JsonbProperty("creationdate") final Long creationDate,
                                           @JsonbProperty("eventtype") final String eventType,
                                           @JsonbProperty("eventmetadata") final String eventMetadata,
                                           @JsonbProperty("eventpayload") final String eventPayload,
                                           @JsonbProperty("materializedstate") final String materializedState) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.version = Objects.requireNonNull(version);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventType = Objects.requireNonNull(eventType);
        this.eventMetadata = Objects.requireNonNull(eventMetadata);
        this.eventPayload = Objects.requireNonNull(eventPayload);
        this.materializedState = Objects.requireNonNull(materializedState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumJsonbAggregateRootEvent)) return false;
        DebeziumJsonbAggregateRootEvent jsonBAggregateRootEventDebezium = (DebeziumJsonbAggregateRootEvent) o;
        return Objects.equals(aggregateRootType, jsonBAggregateRootEventDebezium.aggregateRootType) &&
                Objects.equals(aggregateRootId, jsonBAggregateRootEventDebezium.aggregateRootId) &&
                Objects.equals(version, jsonBAggregateRootEventDebezium.version) &&
                Objects.equals(creationDate, jsonBAggregateRootEventDebezium.creationDate) &&
                Objects.equals(eventType, jsonBAggregateRootEventDebezium.eventType) &&
                Objects.equals(eventMetadata, jsonBAggregateRootEventDebezium.eventMetadata) &&
                Objects.equals(eventPayload, jsonBAggregateRootEventDebezium.eventPayload) &&
                Objects.equals(materializedState, jsonBAggregateRootEventDebezium.materializedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId, version, creationDate, eventType, eventMetadata, eventPayload, materializedState);
    }

    @Override
    public String toString() {
        return "DebeziumJsonbAggregateRootEvent{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetadata='" + eventMetadata + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                ", materializedState='" + materializedState + '\'' +
                '}';
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public Long version() {
        return version;
    }

    public Long creationDate() {
        return creationDate;
    }

    public String eventType() {
        return eventType;
    }

    public String eventMetadata() {
        return eventMetadata;
    }

    public String eventPayload() {
        return eventPayload;
    }

    public String materializedState() {
        return materializedState;
    }

    public AggregateRootEventId aggregateRootEventId() {
        return new DebeziumJsonbAggregateRootEventId(this);
    }

}
