package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.EventConsumer;
import com.damdamdeo.eventdataspreader.event.api.Event;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NullEventConsumer implements EventConsumer {

    @Override
    public void consume(final Event event) {
    }

    @Override
    public String aggregateRootType() {
        return null;
    }

    @Override
    public String eventType() {
        return null;
    }

}
