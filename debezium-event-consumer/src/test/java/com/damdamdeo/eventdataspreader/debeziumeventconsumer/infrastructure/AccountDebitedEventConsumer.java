package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.event.api.Event;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountDebitedEventConsumer implements EventConsumer {

    @Override
    public void consume(final Event event) {
        // Nothing to do
    }

    @Override
    public String aggregateRootType() {
        return "AccountAggregate";
    }

    @Override
    public String eventType() {
        return "AccountDebited";
    }

}
