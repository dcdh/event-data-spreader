package com.damdamdeo.eventdataspreader.eventsourcing.api;

import java.util.Optional;

public interface SecretStore {

    void store(String path, String secret);

    Optional<String> get(String path);

}
