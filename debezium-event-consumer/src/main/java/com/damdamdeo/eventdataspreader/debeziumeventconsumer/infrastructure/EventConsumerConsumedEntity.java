package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumerConsumed;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Table(name = "EventConsumerConsumed")
@NamedQuery(name = "EventConsumerConsumed.getConsumedEventsForEventId",
        query = "SELECT e.eventConsumerId.consumerClassName FROM EventConsumerConsumedEntity e WHERE e.eventConsumerId.eventId = :eventId")
@Entity
public class EventConsumerConsumedEntity implements EventConsumerConsumed {

    @EmbeddedId
    private EventConsumerId eventConsumerId;

    @NotNull
    private Date consumedAt;

    public EventConsumerConsumedEntity() {}

    public EventConsumerConsumedEntity(final EventConsumerId eventConsumerId,
                                       final Date consumedAt) {
        this.eventConsumerId = Objects.requireNonNull(eventConsumerId);
        this.consumedAt = Objects.requireNonNull(consumedAt);
    }

    public EventConsumerId eventConsumerId() {
        return eventConsumerId;
    }

    @Override
    public UUID eventId() {
        return eventConsumerId.eventId();
    }

    @Override
    public String consumerClassName() {
        return eventConsumerId.consumerClassName();
    }

    @Override
    public Date consumedAt() {
        return consumedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventConsumerConsumedEntity)) return false;
        EventConsumerConsumedEntity that = (EventConsumerConsumedEntity) o;
        return Objects.equals(eventConsumerId, that.eventConsumerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventConsumerId);
    }

    @Override
    public String toString() {
        return "EventConsumerConsumedEntity{" +
                "eventConsumerId=" + eventConsumerId +
                ", consumedAt=" + consumedAt +
                '}';
    }
}
