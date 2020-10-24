package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class DebeziumJsonbEventInKeyRecord {

    private final String aggregateRootType;

    private final String aggregateRootId;

    private final Long version;

    @JsonbCreator
    public DebeziumJsonbEventInKeyRecord(@JsonbProperty("aggregateroottype") final String aggregateRootType,
                                         @JsonbProperty("aggregaterootid") final String aggregateRootId,
                                         @JsonbProperty("version") final Long version) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.version = Objects.requireNonNull(version);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumJsonbEventInKeyRecord)) return false;
        DebeziumJsonbEventInKeyRecord that = (DebeziumJsonbEventInKeyRecord) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId, version);
    }

    @Override
    public String toString() {
        return "DebeziumJsonbEventInKeyRecord{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }
}
