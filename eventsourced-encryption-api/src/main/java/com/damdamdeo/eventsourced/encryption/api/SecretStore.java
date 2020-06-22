package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

public interface SecretStore {

    AggregateRootSecret store(String aggregateRootType, String aggregateRootId, String secret);

    AggregateRootSecret read(String aggregateRootType, String aggregateRootId);

    // TODO method void anonymize(String aggregateRootType, String aggregateRootId)
}
