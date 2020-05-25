package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventMetadataConsumer;

import java.util.Objects;

public class ExecutedByAggregateRootEventMetadataConsumer extends AggregateRootEventMetadataConsumer {

    private final String executedBy;

    public ExecutedByAggregateRootEventMetadataConsumer(final String executedBy) {
        this.executedBy = executedBy;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutedByAggregateRootEventMetadataConsumer that = (ExecutedByAggregateRootEventMetadataConsumer) o;
        return Objects.equals(executedBy, that.executedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executedBy);
    }
}
