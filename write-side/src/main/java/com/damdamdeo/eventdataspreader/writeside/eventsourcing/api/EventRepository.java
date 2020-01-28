package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.List;

public interface EventRepository {

    EncryptedEventSecret save(List<Event> events);

    List<Event> load(String aggregateRootId, String aggregateRootType);

}
