package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EventConsumerId implements Serializable {

    @Type(type = "pg-uuid")
    private UUID eventId;

    @NotNull
    @Column(columnDefinition="TEXT")
    private String consumerClassName;

    public EventConsumerId() {}

    public EventConsumerId(final UUID eventId,
                           final Class consumerClassName) {
        this.eventId = Objects.requireNonNull(eventId);
        this.consumerClassName = Objects.requireNonNull(consumerClassName.getName());
    }

    public UUID eventId() {
        return eventId;
    }

    public String consumerClassName() {
        return consumerClassName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventConsumerId)) return false;
        EventConsumerId that = (EventConsumerId) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(consumerClassName, that.consumerClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, consumerClassName);
    }

    @Override
    public String toString() {
        return "EventConsumerId{" +
                "eventId=" + eventId +
                ", consumerClassName='" + consumerClassName + '\'' +
                '}';
    }
}
