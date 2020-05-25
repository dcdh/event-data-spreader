package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.materializedstate;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootMaterializedState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JacksonGiftMaterializedState extends JacksonAggregateRootMaterializedState {

    @JsonCreator
    public JacksonGiftMaterializedState(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                        @JsonProperty("aggregateRootType") final String aggregateRootType,
                                        @JsonProperty("version") final Long version,
                                        @JsonProperty("name") final String name,
                                        @JsonProperty("offeredTo") final String offeredTo) {
    }

}
