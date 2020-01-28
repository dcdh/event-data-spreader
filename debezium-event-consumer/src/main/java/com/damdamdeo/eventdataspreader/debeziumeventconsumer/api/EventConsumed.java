package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import java.util.List;

public interface EventConsumed {

    String eventId();

    Boolean consumed();

    List<? extends EventConsumerConsumed> eventConsumerConsumeds();

}
