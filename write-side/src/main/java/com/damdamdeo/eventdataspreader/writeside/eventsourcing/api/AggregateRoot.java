package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AggregateRoot implements Serializable {
    private final transient List<AggregateRootEvent> unsavedAggregateRootEvents = new LinkedList<>();
    protected String aggregateRootId;
    protected Long version = -1l;
    private String aggregateRootType;

    @SuppressWarnings("unchecked")
    protected void apply(final AggregateRootEventPayload aggregateRootEventPayload, final AggregateRootEventMetadata aggregateRootEventMetaData) {
        aggregateRootEventPayload.apply(this);
        this.version++;
        final String eventAggregateRootId = Objects.requireNonNull(aggregateRootEventPayload.aggregateRootId(), "Aggregate root id can't be null");
        Validate.validState(this.aggregateRootId == null ? true : this.aggregateRootId.equals(eventAggregateRootId), "Aggregate root id and event aggregate root id mismatch");
        this.aggregateRootId = eventAggregateRootId;
        this.aggregateRootType = aggregateRootEventPayload.aggregateRootType();
        final AggregateRootEvent aggregateRootEventToApply = new AggregateRootEvent(
                new EventSourcedAggregateRootEventId(this.aggregateRootId, this.aggregateRootType, this.version),
                aggregateRootEventPayload.eventPayloadName(),
                LocalDateTime.now(),
                aggregateRootEventPayload,
                aggregateRootEventMetaData);
        this.unsavedAggregateRootEvents.add(aggregateRootEventToApply);
    }

    public void loadFromHistory(final List<AggregateRootEvent> aggregateRootEvents) {
        Validate.validState(this.aggregateRootId == null, "Aggregate Root already loaded from history");
        Validate.validState(aggregateRootEvents.stream().map(AggregateRootEvent::aggregateRootId).distinct().count() <= 1, "Aggregate Root ids events mismatch");
        aggregateRootEvents.forEach(event -> {
            this.aggregateRootId = event.aggregateRootId();
            event.eventPayload().apply(this);
            this.version = event.version();
        });
    }

    public List<AggregateRootEvent> unsavedEvents() {
        return unsavedAggregateRootEvents.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        Collections::unmodifiableList));
    }

    public void deleteUnsavedEvents() {
        unsavedAggregateRootEvents.clear();
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
