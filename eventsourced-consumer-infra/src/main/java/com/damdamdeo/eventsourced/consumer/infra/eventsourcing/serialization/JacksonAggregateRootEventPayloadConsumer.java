package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.UnsupportedAggregateRootEventPayloadConsumer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type",
        defaultImpl = UnsupportedAggregateRootEventPayloadConsumer.class)
public abstract class JacksonAggregateRootEventPayloadConsumer {
}
