package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventId;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public final class DebeziumEventId implements EventId {

    private static final String EVENT_AGGREGATE_ROOT_ID = "aggregaterootid";
    private static final String EVENT_AGGREGATE_ROOT_TYPE = "aggregateroottype";
    private static final String EVENT_VERSION = "version";

    private final String aggregateRootId;
    private final String aggregateRootType;
    private final Long version;

    public DebeziumEventId(final String aggregateRootId, final String aggregateRootType, final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

    public DebeziumEventId(final JsonObject after) {
        this(after.getString(EVENT_AGGREGATE_ROOT_ID),
                after.getString(EVENT_AGGREGATE_ROOT_TYPE),
                after.getLong(EVENT_VERSION));
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumEventId)) return false;
        DebeziumEventId that = (DebeziumEventId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, version);
    }

    @Override
    public String toString() {
        return "DebeziumEventId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                '}';
    }
}
