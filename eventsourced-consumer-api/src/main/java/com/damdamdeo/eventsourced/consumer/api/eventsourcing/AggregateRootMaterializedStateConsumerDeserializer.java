package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.util.Optional;

public interface AggregateRootMaterializedStateConsumerDeserializer {

    AggregateRootMaterializedStateConsumer deserialize(Optional<AggregateRootSecret> aggregateRootSecret, String materializedState);

}
