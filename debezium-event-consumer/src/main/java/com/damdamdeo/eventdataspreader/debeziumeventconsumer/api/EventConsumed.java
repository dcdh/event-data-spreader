package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.util.List;

public interface EventConsumed {

    EventId eventId();

    Boolean consumed();

    List<? extends EventConsumerConsumed> eventConsumerConsumeds();

}
