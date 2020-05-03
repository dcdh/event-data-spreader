package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.util.Date;

public interface EventConsumerConsumed {

    EventId eventId();

    String consumerClassName();

    Date consumedAt();

    String gitCommitId();

}
