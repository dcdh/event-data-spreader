package com.damdamdeo.eventsourced.mutable.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.List;

public interface EventRepository<T extends AggregateRoot, EVENT_PAYLOAD_INFRA> {

    void save(AggregateRootEvent aggregateRootEvent, T aggregateRoot);

    List<EncryptedAggregateRootEvent<EVENT_PAYLOAD_INFRA>> loadOrderByVersionASC(AggregateRootId aggregateRootId);

    List<EncryptedAggregateRootEvent<EVENT_PAYLOAD_INFRA>> loadOrderByVersionASC(AggregateRootId aggregateRootId, Long version);

}
