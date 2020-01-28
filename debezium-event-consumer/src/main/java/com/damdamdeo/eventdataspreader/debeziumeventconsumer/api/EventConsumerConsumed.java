package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import java.util.Date;

public interface EventConsumerConsumed {

    String eventId();

    String consumerClassName();

    Date consumedAt();

    String gitCommitId();

}
