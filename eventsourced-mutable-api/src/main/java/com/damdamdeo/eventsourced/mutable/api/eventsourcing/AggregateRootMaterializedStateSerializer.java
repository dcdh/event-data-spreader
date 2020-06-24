package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Secret;

public interface AggregateRootMaterializedStateSerializer {

    String serialize(Secret secret, AggregateRoot aggregateRoot);

}
