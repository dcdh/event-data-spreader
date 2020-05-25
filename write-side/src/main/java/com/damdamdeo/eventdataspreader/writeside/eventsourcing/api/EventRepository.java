package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import java.util.List;

public interface EventRepository {

    void save(List<AggregateRootEvent> aggregateRootEvents);

    List<AggregateRootEvent> loadOrderByVersionASC(String aggregateRootId, String aggregateRootType);

}
