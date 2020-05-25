package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.materializedstate;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootMaterializedState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public abstract class JacksonAccountMaterializedState extends JacksonAggregateRootMaterializedState {

    @JsonCreator
    public JacksonAccountMaterializedState(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                           @JsonProperty("aggregateRootType") final String aggregateRootType,
                                           @JsonProperty("version") final Long version,
                                           @JsonProperty("owner") final String owner,
                                           @JsonProperty("balance") final BigDecimal balance) {
    }

}
