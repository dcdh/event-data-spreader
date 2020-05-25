package com.damdamdeo.eventdataspreader.queryside.infrastructure.consumer.aggregateevent.payload;

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventPayloadConsumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public abstract class JacksonAccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer extends JacksonAggregateRootEventPayloadConsumer {

    @JsonCreator
    public JacksonAccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer(@JsonProperty("owner") final String owner,
                                                                                      @JsonProperty("balance") final BigDecimal balance) {
    }

}
