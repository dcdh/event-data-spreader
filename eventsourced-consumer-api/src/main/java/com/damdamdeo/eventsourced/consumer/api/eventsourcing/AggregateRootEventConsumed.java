package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import java.util.List;

public interface AggregateRootEventConsumed {

    AggregateRootEventId eventId();

    Boolean consumed();

    List<? extends AggregateRootEventConsumerConsumed> aggregateRootEventConsumerConsumeds();

}
