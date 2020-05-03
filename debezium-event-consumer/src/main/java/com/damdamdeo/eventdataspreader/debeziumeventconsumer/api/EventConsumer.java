package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.Event;

public interface EventConsumer<T extends Event> {

    void consume(Event event);

    String aggregateRootType();

    String eventType();

}
