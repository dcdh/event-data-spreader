package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TestAggregateRootEventPayload extends AggregateRootEventPayload<AggregateRoot> {

    private final String dummy;

    @JsonCreator
    public TestAggregateRootEventPayload(@JsonProperty("dummy") final String dummy) {
        this.dummy = Objects.requireNonNull(dummy);
    }

    @Override
    protected void apply(AggregateRoot aggregateRoot) {

    }

    @Override
    public String eventName() {
        return "eventName";
    }

    @Override
    public String aggregateRootId() {
        return "aggregateRootId";
    }

    @Override
    public String aggregateRootType() {
        return "TestAggregateRootEvent";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestAggregateRootEventPayload that = (TestAggregateRootEventPayload) o;
        return Objects.equals(dummy, that.dummy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dummy);
    }
}