package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.DefaultAggregateRootEventId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AggregateRoot implements Serializable {
    private final transient List<AggregateRootEvent> unsavedAggregateRootEvents = new LinkedList<>();
    private String aggregateRootId;
    private Long version = -1l;
    private String aggregateRootType;

    public AggregateRoot() {}

    public AggregateRoot(final String aggregateRootId,
                         final String aggregateRootType,
                         final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

    @SuppressWarnings("unchecked")
    protected final void apply(final AggregateRootEventPayload aggregateRootEventPayload, final AggregateRootEventMetadata aggregateRootEventMetaData) {
        Validate.validState(this.aggregateRootId == null ? true : this.aggregateRootId.equals(aggregateRootEventPayload.aggregateRootId().aggregateRootId()),
                "Aggregate root id and event aggregate root id mismatch");
        Validate.validState(this.aggregateRootType == null ? true : this.aggregateRootType.equals(aggregateRootEventPayload.aggregateRootId().aggregateRootType()),
                "Aggregate root type and event aggregate root type mismatch");
        aggregateRootEventPayload.apply(this);
        this.version++;
        this.aggregateRootId = aggregateRootEventPayload.aggregateRootId().aggregateRootId();
        this.aggregateRootType = aggregateRootEventPayload.aggregateRootId().aggregateRootType();
        final AggregateRootEvent aggregateRootEventToApply = new AggregateRootEvent(
                new DefaultAggregateRootEventId(aggregateRootEventPayload.aggregateRootId(), this.version),
                aggregateRootEventPayload.eventPayloadName(),
                LocalDateTime.now(),
                aggregateRootEventPayload,
                aggregateRootEventMetaData);
        this.unsavedAggregateRootEvents.add(aggregateRootEventToApply);
    }

    public final void loadFromHistory(final List<AggregateRootEvent> aggregateRootEvents) {
        Validate.validState(this.aggregateRootId == null, "Aggregate Root already loaded from history");
        Validate.validState(aggregateRootEvents.stream()
                .map(AggregateRootEvent::aggregateRootId)
                .distinct().count() <= 1, "Aggregate Root ids events mismatch");
        aggregateRootEvents.forEach(event -> {
            this.aggregateRootId = event.aggregateRootId().aggregateRootId();
            this.aggregateRootType = event.aggregateRootId().aggregateRootType();
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

    public Long version() {
        return version;
    }

    public AggregateRootId aggregateRootId() {
        return new AggregateRootId() {
            @Override
            public String aggregateRootId() {
                return aggregateRootId;
            }

            @Override
            public String aggregateRootType() {
                return aggregateRootType;
            }
        };
    }

}
