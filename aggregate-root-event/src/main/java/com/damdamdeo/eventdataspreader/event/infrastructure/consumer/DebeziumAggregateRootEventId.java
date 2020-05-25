package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.event.api.AggregateRootId;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public final class DebeziumAggregateRootEventId implements AggregateRootEventId {

    public static final class DebeziumAggregateRootId implements AggregateRootId {

        private final String aggregateRootId;
        private final String aggregateRootType;

        public DebeziumAggregateRootId(final String aggregateRootId, final String aggregateRootType) {
            this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
            this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DebeziumAggregateRootId that = (DebeziumAggregateRootId) o;
            return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                    Objects.equals(aggregateRootType, that.aggregateRootType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aggregateRootId, aggregateRootType);
        }

        @Override
        public String toString() {
            return "DebeziumAggregateRootId{" +
                    "aggregateRootId='" + aggregateRootId + '\'' +
                    ", aggregateRootType='" + aggregateRootType + '\'' +
                    '}';
        }
    }

    private static final String EVENT_AGGREGATE_ROOT_ID = "aggregaterootid";
    private static final String EVENT_AGGREGATE_ROOT_TYPE = "aggregateroottype";
    private static final String EVENT_VERSION = "version";

    private final AggregateRootId aggregateRootId;
    private final Long version;

    public DebeziumAggregateRootEventId(final String aggregateRootId, final String aggregateRootType, final Long version) {
        this.aggregateRootId = new DebeziumAggregateRootId(aggregateRootId, aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

    public DebeziumAggregateRootEventId(final JsonObject after) {
        this(after.getString(EVENT_AGGREGATE_ROOT_ID),
                after.getString(EVENT_AGGREGATE_ROOT_TYPE),
                after.getLong(EVENT_VERSION));
    }

    @Override
    public AggregateRootId aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebeziumAggregateRootEventId that = (DebeziumAggregateRootEventId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, version);
    }

    @Override
    public String toString() {
        return "DebeziumAggregateRootEventId{" +
                "aggregateRootId=" + aggregateRootId +
                ", version=" + version +
                '}';
    }
}
