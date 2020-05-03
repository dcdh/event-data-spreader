package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.Event;

public interface EventConsumer {

    void consume(Event event);

}
