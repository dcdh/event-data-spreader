package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import java.util.Date;
import java.util.UUID;

public interface EventConsumerConsumed {

    UUID eventId();

    String consumerClassName();

    Date consumedAt();

    String gitCommitId();

}
