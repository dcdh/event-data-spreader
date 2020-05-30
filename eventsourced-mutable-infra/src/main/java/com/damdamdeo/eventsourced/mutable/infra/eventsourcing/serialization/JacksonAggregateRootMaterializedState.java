package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type")
public abstract class JacksonAggregateRootMaterializedState {

}
