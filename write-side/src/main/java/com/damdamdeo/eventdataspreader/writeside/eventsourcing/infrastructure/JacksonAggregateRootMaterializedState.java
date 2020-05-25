package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type")
public abstract class JacksonAggregateRootMaterializedState {

}
