package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public abstract class JacksonAccountAggregateAccountDebitedAggregateRootEventPayloadConsumer extends JacksonAggregateRootEventPayloadConsumer {

    @JsonCreator
    public JacksonAccountAggregateAccountDebitedAggregateRootEventPayloadConsumer(@JsonProperty("owner") final String owner,
                                                                                  @JsonProperty("balance") final BigDecimal balance) {
    }

}
