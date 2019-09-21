package com.damdamdeo.eventdataspreader.writeside.user.type;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types.EventMetadataAdapter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class DefaultEventMetadataAdapter implements EventMetadataAdapter,
        JsonbAdapter<EventMetadata, JsonObject> // Quick fix an issue with yasson
    {

    @Override
    public JsonObject adaptToJson(final EventMetadata eventMetadata) {
        return Json.createObjectBuilder()
                .add("executedBy", ((DefaultEventMetadata) eventMetadata).executedBy())
                .build();
    }

    @Override
    public EventMetadata adaptFromJson(final JsonObject eventMetadata) {
        return new DefaultEventMetadata(eventMetadata.getString("executedBy"));
    }

}
