package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;

import java.time.LocalDateTime;

public interface AggregateRootEventConsumerConsumed {

    AggregateRootEventId eventId();

    String consumerClassName();

    LocalDateTime consumedAt();

    String gitCommitId();

}
