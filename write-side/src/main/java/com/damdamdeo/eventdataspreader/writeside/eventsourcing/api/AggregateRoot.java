package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import java.io.Serializable;
import java.util.*;

public abstract class AggregateRoot implements Serializable {

    private final transient List<Event> unsavedEvents = new LinkedList<>();
    protected String aggregateRootId;
    protected Long version = -1l;

    protected void apply(final EventPayload eventPayload) {
        this.apply(eventPayload, null);
    }

    protected void apply(final EventPayload eventPayload, final EventMetadata eventMetaData) {
        eventPayload.apply(this);
        this.version++;
        this.aggregateRootId = eventPayload.eventPayloadIdentifier().aggregateRootId();
        this.unsavedEvents.add(new Event(UUID.randomUUID(),
                this.version,
                new Date(),
                eventPayload,
                eventMetaData
        ));
    }

    public void loadFromHistory(final List<Event> events) {
        events.forEach(event -> {
            this.aggregateRootId = event.aggregateRootId();
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
