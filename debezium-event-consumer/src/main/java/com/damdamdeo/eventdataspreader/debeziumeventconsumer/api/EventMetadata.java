package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
public interface EventMetadata {
}
