package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultEventMetadata implements EventMetadata {

    private final String executedBy;

    @JsonCreator
    public DefaultEventMetadata(@JsonProperty("executedBy") final String executedBy) {
        this.executedBy = executedBy;
    }

    public String getExecutedBy() {
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
