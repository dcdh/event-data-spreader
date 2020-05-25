package com.damdamdeo.eventdataspreader.writeside.aggregate.metadata;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventMetadata;

import java.util.Objects;

public final class UserAggregateRootEventMetadata extends AggregateRootEventMetadata {

    private final String executedBy;

    public UserAggregateRootEventMetadata(final String executedBy) {
        this.executedBy = Objects.requireNonNull(executedBy);
    }

    public String executedBy() {
        return executedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAggregateRootEventMetadata that = (UserAggregateRootEventMetadata) o;
        return Objects.equals(executedBy, that.executedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executedBy);
    }

    @Override
    public String toString() {
        return "UserAggregateRootEventMetadata{" +
                "executedBy='" + executedBy + '\'' +
                '}';
    }
}
