package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import java.util.List;

public interface EventRepository {

    void save(List<AggregateRootEvent> aggregateRootEvents);

    List<AggregateRootEvent> loadOrderByVersionASC(String aggregateRootId, String aggregateRootType);

}
