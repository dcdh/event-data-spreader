package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.event.api.Event;

public interface EventConsumer {

    void consume(Event event);

    String aggregateRootType();

    String eventType();

}
