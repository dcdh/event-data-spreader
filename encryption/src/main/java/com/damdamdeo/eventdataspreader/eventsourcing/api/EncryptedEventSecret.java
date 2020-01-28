package com.damdamdeo.eventdataspreader.eventsourcing.api;

import java.util.Date;

public interface EncryptedEventSecret {

    String aggregateRootId();

    String aggregateRootType();

    Date creationDate();

    String secret();

}
