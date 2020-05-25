package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JacksonExecutedByAggregateRootEventMetadataConsumer extends JacksonAggregateRootEventMetadataConsumer {

    @JsonCreator
    public JacksonExecutedByAggregateRootEventMetadataConsumer(@JsonProperty("executedBy") final String executedBy) {
    }

}
