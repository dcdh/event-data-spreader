package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public interface AggregateRootAdapter extends JsonbAdapter<AggregateRoot, JsonObject> {
}
