package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import java.util.List;

public interface EventConsumed {

    EventId eventId();

    Boolean consumed();

    List<? extends EventConsumerConsumed> eventConsumerConsumeds();

}
