package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayload extends JacksonAggregateRootEventPayload {

    @JsonCreator
    public JacksonGiftAggregateRootGiftBoughtAggregateRootEventPayload(@JsonProperty("name") final String name) {
    }

}
