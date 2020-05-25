package com.damdamdeo.eventdataspreader.writeside.infrastructure.eventsourcing.aggregateevent;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class JacksonGiftAggregateRootGiftOfferedAggregateRootEventPayload extends JacksonAggregateRootEventPayload {

    @JsonCreator
    public JacksonGiftAggregateRootGiftOfferedAggregateRootEventPayload(@JsonProperty("name") final String name,
                                                                        @JsonProperty("offeredTo") final String offeredTo) {
    }

}
