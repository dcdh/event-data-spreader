package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;

import java.util.List;

public interface AggregateRootEventConsumed {

    AggregateRootEventId eventId();

    Boolean consumed();

    List<? extends AggregateRootEventConsumerConsumed> aggregateRootEventConsumerConsumeds();

}
