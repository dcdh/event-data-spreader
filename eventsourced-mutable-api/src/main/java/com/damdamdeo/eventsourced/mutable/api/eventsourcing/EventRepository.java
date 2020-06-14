package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.util.List;
import java.util.Optional;

public interface EventRepository {

    void save(List<AggregateRootEvent> aggregateRootEvents, Optional<AggregateRootSecret> aggregateRootSecret);

    void saveMaterializedState(AggregateRoot aggregateRoot, Optional<AggregateRootSecret> aggregateRootSecret);

    List<AggregateRootEvent> loadOrderByVersionASC(String aggregateRootId, String aggregateRootType, Optional<AggregateRootSecret> aggregateRootSecret);

    List<AggregateRootEvent> loadOrderByVersionASC(String aggregateRootId, String aggregateRootType, Optional<AggregateRootSecret> aggregateRootSecret, Long version);

}
