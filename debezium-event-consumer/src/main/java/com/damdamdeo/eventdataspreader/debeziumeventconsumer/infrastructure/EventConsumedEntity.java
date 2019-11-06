package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;


import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumed;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumerConsumed;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Table(name = "EventConsumed")
@Entity
@NamedQuery(name = "Events.findByEventId",
        query = "SELECT e FROM EventConsumedEntity e  LEFT JOIN FETCH e.eventConsumerEntities WHERE e.eventId = :eventId")
public class EventConsumedEntity implements EventConsumed {

    @Id
    @Type(type = "pg-uuid")
    private UUID eventId;

    @NotNull
    private Boolean consumed;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "EventConsumerConsumedEntity_eventConsumerId")
    private List<EventConsumerConsumedEntity> eventConsumerEntities;

    public EventConsumedEntity() {}

    public EventConsumedEntity(final UUID eventId) {
        this.eventId = Objects.requireNonNull(eventId);
        this.consumed = Boolean.FALSE;
        this.eventConsumerEntities = new ArrayList<>();
    }

    public void addNewEventConsumerConsumed(final Class consumerClass,
                                            final Date consumedAt) {
        eventConsumerEntities.add(
                new EventConsumerConsumedEntity(
                        new EventConsumerId(eventId, consumerClass),
                        consumedAt));
    }

    public void markAsConsumed() {
        consumed = Boolean.TRUE;
    }

    @Override
    public UUID eventId() {
        return eventId;
    }

    @Override
    public Boolean consumed() {
        return consumed;
    }

    @Override
    public List<? extends EventConsumerConsumed> eventConsumerConsumeds() {
        return eventConsumerEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventConsumedEntity)) return false;
        EventConsumedEntity that = (EventConsumedEntity) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "EventConsumedEntity{" +
                "eventId=" + eventId +
                ", consumed=" + consumed +
                ", eventConsumerEntities=" + eventConsumerEntities +
                '}';
    }
}
