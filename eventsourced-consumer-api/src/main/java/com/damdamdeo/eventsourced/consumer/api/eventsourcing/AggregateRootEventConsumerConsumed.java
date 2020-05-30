package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import java.time.LocalDateTime;

public interface AggregateRootEventConsumerConsumed {

    AggregateRootEventId eventId();

    String consumerClassName();

    LocalDateTime consumedAt();

    String gitCommitId();

}
