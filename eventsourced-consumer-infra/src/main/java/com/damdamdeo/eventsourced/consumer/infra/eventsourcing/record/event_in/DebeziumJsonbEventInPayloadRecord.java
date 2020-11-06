package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.DebeziumJsonbAggregateRootEventConsumable;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class DebeziumJsonbEventInPayloadRecord {

    private final DebeziumJsonbAggregateRootEvent after;

    private final String operation;

    @JsonbCreator
    public DebeziumJsonbEventInPayloadRecord(@JsonbProperty("after") final DebeziumJsonbAggregateRootEvent after,
                                             @JsonbProperty("op") final String operation) {
        this.after = Objects.requireNonNull(after);
        this.operation = Objects.requireNonNull(operation);
    }

    public DebeziumJsonbAggregateRootEventConsumable debeziumJsonbAggregateRootEventConsumable() {
        return new DebeziumJsonbAggregateRootEventConsumable(after);
    }

    public String operation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumJsonbEventInPayloadRecord)) return false;
        DebeziumJsonbEventInPayloadRecord that = (DebeziumJsonbEventInPayloadRecord) o;
        return Objects.equals(after, that.after) &&
                Objects.equals(operation, that.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(after, operation);
    }

    @Override
    public String toString() {
        return "DebeziumJsonbEventInPayloadRecord{" +
                "after=" + after +
                ", operation='" + operation + '\'' +
                '}';
    }
}
