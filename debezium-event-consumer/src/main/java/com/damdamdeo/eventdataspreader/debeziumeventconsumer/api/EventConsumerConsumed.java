package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import java.util.Date;

public interface EventConsumerConsumed {

    EventId eventId();

    String consumerClassName();

    Date consumedAt();

    String gitCommitId();

}
