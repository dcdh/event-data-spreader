package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AggregateRoot {
    private final transient List<AggregateRootEvent> unsavedAggregateRootEvents = new LinkedList<>();
    private String aggregateRootId;
    private Long version = -1l;
    private String aggregateRootType;

    public AggregateRoot(final String aggregateRootId) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = this.getClass().getSimpleName();
    }

    public AggregateRoot(final String aggregateRootId, Long version) {
        this(aggregateRootId);
        this.version = Objects.requireNonNull(version);
        Validate.validState(version >= 0l);
    }

    @SuppressWarnings("unchecked")
    public final void apply(final String eventType,
                            final AggregateRootEventPayload aggregateRootEventPayload) {
        Validate.notNull(eventType, "eventType can't be null");
        Validate.notNull(aggregateRootEventPayload, "aggregateRootEventPayload can't be null");
        aggregateRootEventPayload.apply(this);
        this.version++;
        final AggregateRootEvent aggregateRootEventToApply = new AggregateRootEvent(
                new DefaultAggregateRootEventId(aggregateRootId(), this.version),
                eventType,
                LocalDateTime.now(ZoneOffset.UTC),
                aggregateRootEventPayload);
        this.unsavedAggregateRootEvents.add(aggregateRootEventToApply);
    }

    public final void loadFromHistory(final List<AggregateRootEvent> aggregateRootEvents) {
        Validate.validState(Long.valueOf(-1l).equals(this.version), "Aggregate Root already loaded from history");
        Validate.validState(aggregateRootEvents.stream()
                .map(AggregateRootEvent::aggregateRootId)
                .distinct().count() <= 1, "Aggregate Root ids events mismatch");
        Validate.validState(aggregateRootEvents.stream().allMatch(aggregateRootEvent -> aggregateRootId.equals(aggregateRootEvent.aggregateRootId().aggregateRootId())),
                "Aggregate root id and event aggregate root id mismatch");
        Validate.validState(aggregateRootEvents.stream().allMatch(aggregateRootEvent -> aggregateRootType.equals(aggregateRootEvent.aggregateRootId().aggregateRootType())),
                "Aggregate root type and event aggregate root type mismatch");
        aggregateRootEvents.forEach(event -> {
            event.eventPayload().apply(this);
            this.version = event.version();
        });
    }

    public final List<AggregateRootEvent> unsavedEvents() {
        return unsavedAggregateRootEvents.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        Collections::unmodifiableList));
    }

    public final void deleteUnsavedEvents() {
        unsavedAggregateRootEvents.clear();
    }

    public final Long version() {
        return version;
    }

    public final AggregateRootId aggregateRootId() {
        return new ApiAggregateRootId(aggregateRootId, aggregateRootType);
    }

    public final String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateRoot)) return false;
        AggregateRoot that = (AggregateRoot) o;
        return Objects.equals(unsavedAggregateRootEvents, that.unsavedAggregateRootEvents) &&
                Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(version, that.version) &&
                Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unsavedAggregateRootEvents, aggregateRootId, version, aggregateRootType);
    }
}
