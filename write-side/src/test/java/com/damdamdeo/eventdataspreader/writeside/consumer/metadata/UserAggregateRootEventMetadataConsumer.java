package com.damdamdeo.eventdataspreader.writeside.consumer.metadata;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventMetadataConsumer;

import java.util.Objects;

public final class UserAggregateRootEventMetadataConsumer extends AggregateRootEventMetadataConsumer {

    private final String executedBy;

    public UserAggregateRootEventMetadataConsumer(final String executedBy) {
        this.executedBy = Objects.requireNonNull(executedBy);
    }

    public String executedBy() {
        return executedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAggregateRootEventMetadataConsumer that = (UserAggregateRootEventMetadataConsumer) o;
        return Objects.equals(executedBy, that.executedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executedBy);
    }

    @Override
    public String toString() {
        return "UserAggregateRootEventMetadataConsumer{" +
                "executedBy='" + executedBy + '\'' +
                '}';
    }
}

