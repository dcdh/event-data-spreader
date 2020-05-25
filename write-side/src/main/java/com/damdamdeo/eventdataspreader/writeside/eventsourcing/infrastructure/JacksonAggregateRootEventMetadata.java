package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type")
public abstract class JacksonAggregateRootEventMetadata {
}
