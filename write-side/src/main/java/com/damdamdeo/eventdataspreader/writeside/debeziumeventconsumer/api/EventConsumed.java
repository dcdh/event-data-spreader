package com.damdamdeo.eventdataspreader.writeside.debeziumeventconsumer.api;

import java.util.List;
import java.util.UUID;

public interface EventConsumed {

    UUID eventId();

    Boolean consumed();

    List<? extends EventConsumerConsumed> eventConsumerConsumeds();

}
