package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JacksonUnsupportedAggregateRootMaterializedStateConsumer extends JacksonAggregateRootMaterializedStateConsumer {

    @JsonCreator
    public JacksonUnsupportedAggregateRootMaterializedStateConsumer(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                                                    @JsonProperty("aggregateRootType") final String aggregateRootType,
                                                                    @JsonProperty("version") final Long version) {
    }

}
