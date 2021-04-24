package com.damdamdeo.eventsourced.mutable.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;

public interface AggregateRootMaterializedStateRepository {

    void persist(AggregateRootMaterializedState aggregateRootMaterializedState);

    AggregateRootMaterializedState find(AggregateRootId aggregateRootId);

}
