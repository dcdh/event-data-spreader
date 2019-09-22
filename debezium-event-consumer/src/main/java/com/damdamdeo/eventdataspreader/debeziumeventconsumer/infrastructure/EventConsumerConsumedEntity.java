package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumerConsumed;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
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
        this.eventConsumerId = eventConsumerId;
        this.consumedAt = consumedAt;
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

}
