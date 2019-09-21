package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventMetadata;

import java.util.Objects;

public final class DefaultEventMetadata implements EventMetadata {

    private final String executedBy;

    public DefaultEventMetadata(final String executedBy) {
        this.executedBy = Objects.requireNonNull(executedBy);
    }

    public String executedBy() {
        return executedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultEventMetadata)) return false;
        DefaultEventMetadata that = (DefaultEventMetadata) o;
        return Objects.equals(executedBy, that.executedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executedBy);
    }

    @Override
    public String toString() {
        return "DefaultEventMetadata{" +
                "executedBy='" + executedBy + '\'' +
                '}';
    }

}
