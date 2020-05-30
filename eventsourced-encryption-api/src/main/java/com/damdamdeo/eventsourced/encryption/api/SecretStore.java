package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.util.Optional;

public interface SecretStore {

    AggregateRootSecret store(String aggregateRootType, String aggregateRootId, String secret);

    Optional<AggregateRootSecret> read(String aggregateRootType, String aggregateRootId);

    // TODO method void anonymize(String aggregateRootType, String aggregateRootId)
}
