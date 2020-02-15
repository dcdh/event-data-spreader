package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventId;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventConsumedId implements Serializable, EventId {

    @NotNull
    private String aggregateRootId;

    @NotNull
    private String aggregateRootType;

    @NotNull
    private Long version;

    public EventConsumedId() {}

    public EventConsumedId(final String aggregateRootId,
                           final String aggregateRootType,
                           final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

    public EventConsumedId(final EventId eventId) {
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
        if (!(o instanceof EventConsumedId)) return false;
        EventConsumedId that = (EventConsumedId) o;
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
        return "EventConsumedId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                '}';
    }

}
