package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.util.Objects;

public final class JdbcEventId implements EventId {

    private final String aggregateRootId;

    private final String aggregateRootType;

    private final Long version;

    public JdbcEventId(final String aggregateRootId,
                       final String aggregateRootType,
                       final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

    public JdbcEventId(final EventId eventId) {
        this(eventId.aggregateRootId(), eventId.aggregateRootType(), eventId.version());
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
        if (!(o instanceof JdbcEventId)) return false;
        JdbcEventId that = (JdbcEventId) o;
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
        return "EncryptedIdEventEntity{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                '}';
    }
}
