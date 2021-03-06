package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

public interface AggregateRootInstanceCreator {

    <T extends AggregateRoot> T createNewInstance(Class<T> clazz, String aggregateRootId);

}
