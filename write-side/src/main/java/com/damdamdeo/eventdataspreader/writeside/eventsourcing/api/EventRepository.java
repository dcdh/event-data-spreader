package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import java.util.List;

public interface EventRepository {

    void save(List<Event> events);

    List<Event> load(String aggregateRootId, String aggregateRootType);

}
