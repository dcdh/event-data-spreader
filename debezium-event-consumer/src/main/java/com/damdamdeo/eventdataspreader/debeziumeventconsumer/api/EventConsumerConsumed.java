package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.time.LocalDateTime;

public interface EventConsumerConsumed {

    EventId eventId();

    String consumerClassName();

    LocalDateTime consumedAt();

    String gitCommitId();

}
