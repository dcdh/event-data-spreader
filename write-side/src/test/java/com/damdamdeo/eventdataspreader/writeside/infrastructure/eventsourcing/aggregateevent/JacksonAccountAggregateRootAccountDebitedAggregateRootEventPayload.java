package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public abstract class JacksonAccountAggregateRootAccountDebitedAggregateRootEventPayload extends JacksonAggregateRootEventPayload {

    @JsonCreator
    public JacksonAccountAggregateRootAccountDebitedAggregateRootEventPayload(@JsonProperty("owner") final String owner,
                                                                              @JsonProperty("price") final BigDecimal price,
                                                                              @JsonProperty("balance") final BigDecimal balance) {
    }

}
