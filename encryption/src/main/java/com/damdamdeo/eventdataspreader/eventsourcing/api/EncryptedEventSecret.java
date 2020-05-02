package com.damdamdeo.eventdataspreader.eventsourcing.api;

public interface EncryptedEventSecret {

    String aggregateRootType();

    String aggregateRootId();

    String secret();

}
