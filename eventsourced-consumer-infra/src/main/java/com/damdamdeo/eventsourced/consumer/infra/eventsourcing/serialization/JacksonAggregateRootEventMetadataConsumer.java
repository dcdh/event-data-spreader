package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.UnsupportedAggregateRootEventMetadataConsumer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type",
        defaultImpl = UnsupportedAggregateRootEventMetadataConsumer.class
)
public abstract class JacksonAggregateRootEventMetadataConsumer {
}
