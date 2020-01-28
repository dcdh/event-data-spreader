package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "aggregateRootType")
public abstract class AggregateRoot implements Serializable {

    private final transient List<Event> unsavedEvents = new LinkedList<>();
    protected String aggregateRootId;
    protected Long version = -1l;
    private String aggregateRootType;

    protected Event apply(final AggregateRootEventPayload aggregateRootEventPayload, final EventMetadata eventMetaData) {
        aggregateRootEventPayload.apply(this);
        this.version++;
        final String eventAggregateRootId = Objects.requireNonNull(aggregateRootEventPayload.aggregateRootId(), "Aggregate root id can't be null");
        Validate.validState(this.aggregateRootId == null ? true : this.aggregateRootId.equals(eventAggregateRootId), "Aggregate root id and event aggregate root id mismatch");
        this.aggregateRootId = eventAggregateRootId;
        this.aggregateRootType = aggregateRootEventPayload.aggregateRootType();
        final Event eventToApply = new Event(String.format("%30s|%50s|%010d", this.aggregateRootId, this.aggregateRootType, this.version).replace(' ', '*'),
                this.aggregateRootId,
                this.aggregateRootType,
                aggregateRootEventPayload.eventName(),
                this.version,
                new Date(),
                aggregateRootEventPayload,
                eventMetaData);
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
        return unsavedEvents.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        Collections::unmodifiableList));
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

    public String aggregateRootType() {
        return aggregateRootType;
    }

}
