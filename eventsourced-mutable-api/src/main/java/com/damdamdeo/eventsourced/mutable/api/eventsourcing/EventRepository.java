package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Secret;

import java.util.List;

public interface EventRepository {

    void save(List<AggregateRootEvent> aggregateRootEvents, Secret secret);

    void saveMaterializedState(AggregateRoot aggregateRoot, Secret secret);

    List<AggregateRootEvent> loadOrderByVersionASC(String aggregateRootId, String aggregateRootType, Secret secret);

    List<AggregateRootEvent> loadOrderByVersionASC(String aggregateRootId, String aggregateRootType, Secret secret, Long version);

}
