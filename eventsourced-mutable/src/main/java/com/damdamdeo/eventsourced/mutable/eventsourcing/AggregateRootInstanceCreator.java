package com.damdamdeo.eventsourced.mutable.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

public interface AggregateRootInstanceCreator<T> {

    T createNewInstance(AggregateRootId aggregateRootId);

}
