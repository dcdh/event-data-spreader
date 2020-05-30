package com.damdamdeo.eventsourced.model.api;

public interface AggregateRootSecret {

    public static final String SECRET_KEY = "aggregateRootSecret";
    public static final String ENCRYPTION_STRATEGY = "encryptionStrategy";

    AggregateRootId aggregateRootId();

    String secret();

}
