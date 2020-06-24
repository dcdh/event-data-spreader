package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Secret;

public interface AggregateRootMaterializedStateConsumerDeserializer {

    AggregateRootMaterializedStateConsumer deserialize(Secret secret, String materializedState);

}
