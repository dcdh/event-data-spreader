package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.metadata;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventMetadata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JacksonUserAggregateRootEventMetadata extends JacksonAggregateRootEventMetadata {

    @JsonCreator
    public JacksonUserAggregateRootEventMetadata(@JsonProperty("executedBy") final String executedBy) {
    }

}
