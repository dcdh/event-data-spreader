package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type")
public abstract class JacksonAggregateRootEventPayload {
}
