package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import java.io.Serializable;
import java.util.*;

public abstract class AggregateRoot implements Serializable {

    protected String aggregateRootId;
    private final transient List<Event> unsavedEvents = new LinkedList<>();
    protected Long version = -1l;

    public void apply(final EventPayload eventPayload) {
        this.apply(eventPayload, null);
    }

    public void apply(final EventPayload eventPayload, final EventMetadata metaData) {
        eventPayload.apply(this);
        this.version++;
        this.unsavedEvents.add(new Event(UUID.randomUUID(),
                Objects.requireNonNull(aggregateRootId, "aggregateRootId must not be null please ensure it was set by the creational event !"),
                this.getClass().getSimpleName(),
                eventPayload.getClass().getSimpleName().replaceFirst("(^.+)Payload$", "$1"),
                this.version,
                new Date(),
                eventPayload,
                metaData));
    }

    public void loadFromHistory(final List<Event> events) {
        events.forEach(event -> {
            event.eventPayload().apply(this);
            this.version = event.version();
        });
    }

    public List<Event> unsavedEvents() {
        return new ArrayList<Event>(unsavedEvents);
    }

    public void deleteUnsavedEvents() {
        unsavedEvents.clear();
    }

    public Long version() {
        return version;
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

}
