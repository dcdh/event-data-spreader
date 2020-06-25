package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;

public abstract class JacksonUnsupportedAggregateRootEventPayload extends JacksonAggregateRootEventPayload {

    @JsonCreator
    public JacksonUnsupportedAggregateRootEventPayload() {}

}
