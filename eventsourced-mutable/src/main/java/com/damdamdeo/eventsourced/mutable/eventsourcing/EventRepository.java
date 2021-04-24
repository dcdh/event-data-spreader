package com.damdamdeo.eventsourced.mutable.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.List;

public interface EventRepository {

    void save(AggregateRootEvent aggregateRootEvent, AggregateRoot aggregateRoot);

    List<AggregateRootEvent> loadOrderByVersionASC(AggregateRootId aggregateRootId);

    List<AggregateRootEvent> loadOrderByVersionASC(AggregateRootId aggregateRootId, Long version);

}
