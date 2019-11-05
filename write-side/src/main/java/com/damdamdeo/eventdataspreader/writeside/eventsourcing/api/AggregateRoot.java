package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.*;

public abstract class AggregateRoot implements Serializable {

    private final transient List<Event> unsavedEvents = new LinkedList<>();
    protected String aggregateRootId;
    protected Long version = -1l;

    protected Event apply(final EventPayload eventPayload, final EventMetadata eventMetaData) {
        eventPayload.apply(this);
        this.version++;
        final String eventAggregateRootId = Objects.requireNonNull(eventPayload.eventPayloadIdentifier().aggregateRootId(), "Aggregate root id can't be null");
        Validate.validState(this.aggregateRootId == null ? true : this.aggregateRootId.equals(eventAggregateRootId), "Aggregate root id and event aggregate root id mismatch");
        this.aggregateRootId = eventAggregateRootId;
        final Event eventToApply = new Event(UUID.randomUUID(),
                this.version,
                new Date(),
                eventPayload,
                eventMetaData
        );
        this.unsavedEvents.add(eventToApply);
        return eventToApply;
    }

    public void loadFromHistory(final List<Event> events) {
        Validate.validState(this.aggregateRootId == null, "Aggregate Root already loaded from history");
        Validate.validState(events.stream().map(Event::aggregateRootId).distinct().count() <= 1, "Aggregate Root ids events mismatch");
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
