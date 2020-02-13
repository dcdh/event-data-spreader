package com.damdamdeo.eventdataspreader.eventsourcing.api;

import java.util.Optional;

public interface SecretStore {

    void store(String aggregateRootType, String aggregateRootId, String secret);

    Optional<EncryptedEventSecret> read(String aggregateRootType, String aggregateRootId);

}
