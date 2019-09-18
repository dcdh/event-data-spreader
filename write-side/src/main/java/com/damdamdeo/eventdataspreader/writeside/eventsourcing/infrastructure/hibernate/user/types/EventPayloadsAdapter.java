package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;

import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public interface EventPayloadsAdapter extends JsonbAdapter<EventPayload, JsonObject> {
}
