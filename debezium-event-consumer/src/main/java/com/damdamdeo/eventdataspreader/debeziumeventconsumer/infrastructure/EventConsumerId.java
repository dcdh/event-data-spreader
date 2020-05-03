package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventConsumerId implements Serializable, EventId {

    @NotNull
    private String aggregateRootId;

    @NotNull
    private String aggregateRootType;

    @NotNull
    private Long version;

    @NotNull
    @Column(columnDefinition="TEXT")
    private String consumerClassName;

    public EventConsumerId() {}

    public EventConsumerId(final EventId eventId,
                           final Class consumerClassName) {
        this.aggregateRootId = Objects.requireNonNull(eventId).aggregateRootId();
        this.aggregateRootType = Objects.requireNonNull(eventId).aggregateRootType();
        this.version = Objects.requireNonNull(eventId).version();
        this.consumerClassName = Objects.requireNonNull(consumerClassName.getName());
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public Long version() {
        return version;
    }

    public String consumerClassName() {
        return consumerClassName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventConsumerId)) return false;
        EventConsumerId that = (EventConsumerId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(version, that.version) &&
                Objects.equals(consumerClassName, that.consumerClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, version, consumerClassName);
    }

    @Override
    public String toString() {
        return "EventConsumerId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                ", consumerClassName='" + consumerClassName + '\'' +
                '}';
    }
}
