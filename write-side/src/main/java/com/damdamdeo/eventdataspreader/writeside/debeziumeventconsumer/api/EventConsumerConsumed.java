package com.damdamdeo.eventdataspreader.writeside.debeziumeventconsumer.api;

import java.util.Date;
import java.util.UUID;

public interface EventConsumerConsumed {

    UUID eventId();

    String consumerClassName();

    Date consumedAt();

}
