package com.damdamdeo.eventsourced.mutable.kafka.connect.transforms;

import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.data.Struct;

import java.util.Objects;

public final class AggregateRootEventId {

    private final String aggregateRootType;
    private final String aggregateRootId;
    private final Long version;

    public AggregateRootEventId(final Struct key) {
        this.aggregateRootType = Objects.requireNonNull(key.getString("aggregateroottype"));
        this.aggregateRootId = Objects.requireNonNull(key.getString("aggregaterootid"));
        this.version = Objects.requireNonNull(key.getInt64("version"));
    }

    public int targetPartition(final int nbOfPartitionsInEventTopic) {
        // cf. https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/clients/producer/internals/DefaultPartitioner.java
        // hashCode can return negative value. Avoid it by applying a mask to remove the negative bit.
        return Utils.toPositive(new AggregateRootId(aggregateRootType, aggregateRootId).hashCode()) % nbOfPartitionsInEventTopic;
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
        if (!(o instanceof AggregateRootEventId)) return false;
        AggregateRootEventId that = (AggregateRootEventId) o;
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
        return "AggregateRootEventId{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }
}
