package com.damdamdeo.eventsourced.encryption.api;

public interface SecretStore {

    Secret store(String aggregateRootType, String aggregateRootId, String secret);

    Secret read(String aggregateRootType, String aggregateRootId);

    // TODO method void anonymize(String aggregateRootType, String aggregateRootId)
}
