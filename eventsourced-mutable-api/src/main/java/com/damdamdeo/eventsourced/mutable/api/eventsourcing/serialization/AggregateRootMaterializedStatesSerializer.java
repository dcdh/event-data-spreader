package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

public interface AggregateRootMaterializedStatesSerializer {

    String serialize(AggregateRoot aggregateRoot, Secret secret, Encryption encryption);

}
