package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Table(name = "Event")
@Entity
@NamedQuery(name = "Events.findByAggregateRootIdOrderByVersionAsc",
        query = "SELECT e FROM EventEntity e WHERE e.aggregateRootId = :aggregateRootId and e.aggregateRootType = :aggregateRootType ORDER BY e.version ASC")
public class EventEntity {

    @Id
    @Type(type = "pg-uuid")
    private UUID eventId;

    @NotNull
    private String aggregateRootId;

    @NotNull
    private String aggregateRootType;

    @NotNull
    private String eventType;

    @NotNull
    private Long version;

    @NotNull
    private Date creationDate;

    @NotNull
    @Type(type = "jsonbEventMetaData")
    private EventMetadata metaData;

    @NotNull
    @Type(type = "jsonbEventPayload")
    private EventPayload eventPayload;

    public EventEntity() {};

    public EventEntity(final Event event) {
        this.eventId = event.eventId();
        this.aggregateRootId = event.aggregateRootId();
        this.aggregateRootType = event.aggregateRootType();
        this.eventType = event.eventType();
        this.version = event.version();
        this.creationDate = event.creationDate();
        this.metaData = event.eventMetaData();
        this.eventPayload = event.eventPayload();
    }

    public Event toEvent() {
        return new Event(eventId,
                aggregateRootId,
                aggregateRootType,
                eventType,
                version,
                creationDate,
                eventPayload,
                metaData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

}
