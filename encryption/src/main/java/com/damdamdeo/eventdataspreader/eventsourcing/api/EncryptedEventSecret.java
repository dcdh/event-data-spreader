package com.damdamdeo.eventdataspreader.eventsourcing.api;

public interface EncryptedEventSecret {

    String aggregateRootId();

    String aggregateRootType();

    String secret();

}
