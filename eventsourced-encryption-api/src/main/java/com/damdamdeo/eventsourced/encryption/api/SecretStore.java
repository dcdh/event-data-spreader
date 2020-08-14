package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

public interface SecretStore {

    Secret store(AggregateRootId aggregateRootId, String secret);

    Secret read(AggregateRootId aggregateRootId);

}
