package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventMetadata;

import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public interface EventMetadataAdapter extends JsonbAdapter<EventMetadata, JsonObject> {
}
