package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;

public abstract class JacksonUnsupportedAggregateRootEventMetadata extends JacksonAggregateRootEventMetadata {

    @JsonCreator
    public JacksonUnsupportedAggregateRootEventMetadata() {}

}
