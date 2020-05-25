package com.damdamdeo.eventdataspreader.queryside.infrastructure.consumer.aggregateevent.payload;

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.JacksonAggregateRootEventPayloadConsumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer extends JacksonAggregateRootEventPayloadConsumer {

    @JsonCreator
    public JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer(@JsonProperty("name") final String name) {
    }

}
