package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.event.api.Event;

import javax.enterprise.context.Dependent;

@Dependent
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
