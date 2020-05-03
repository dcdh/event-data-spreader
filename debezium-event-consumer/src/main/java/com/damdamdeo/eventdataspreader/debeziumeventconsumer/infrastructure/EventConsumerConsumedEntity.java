package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumerConsumed;
import com.damdamdeo.eventdataspreader.event.api.EventId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Table(name = "EventConsumerConsumed")
@NamedQuery(name = "EventConsumerConsumed.getConsumedEventsForEventId",
        query = "SELECT e.eventConsumerId.consumerClassName FROM EventConsumerConsumedEntity e " +
                "WHERE e.eventConsumerId.aggregateRootId = :aggregateRootId " +
                "AND e.eventConsumerId.aggregateRootType = :aggregateRootType " +
                "AND e.eventConsumerId.version = :version")
@Entity
public class EventConsumerConsumedEntity implements EventConsumerConsumed {

    @EmbeddedId
    private EventConsumerId eventConsumerId;

    @NotNull
    private Date consumedAt;

    @NotNull
    private String gitCommitId;

    public EventConsumerConsumedEntity() {}

    public EventConsumerConsumedEntity(final EventConsumerId eventConsumerId,
                                       final Date consumedAt,
                                       final String gitCommitId) {
        this.eventConsumerId = Objects.requireNonNull(eventConsumerId);
        this.consumedAt = Objects.requireNonNull(consumedAt);
        this.gitCommitId = Objects.requireNonNull(gitCommitId);
    }

    public EventConsumerId eventConsumerId() {
        return eventConsumerId;
    }

    @Override
    public EventId eventId() {
        return eventConsumerId;
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
    public String gitCommitId() {
        return gitCommitId;
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
                ", gitCommitId='" + gitCommitId + '\'' +
                '}';
    }
}
