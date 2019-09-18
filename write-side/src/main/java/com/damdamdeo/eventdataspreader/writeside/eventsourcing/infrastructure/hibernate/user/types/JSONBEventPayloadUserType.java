package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Iterator;
import java.util.ServiceLoader;

public class JSONBEventPayloadUserType extends AbstractUserType {

    public static final JSONBEventPayloadUserType INSTANCE = new JSONBEventPayloadUserType();

    private static final Jsonb MAPPER;

    static {
        final JsonbConfig jsonbConfig = new JsonbConfig().withFormatting(true);
        final ServiceLoader<EventPayloadsAdapter> loader = ServiceLoader.load(EventPayloadsAdapter.class);
        final Iterator<EventPayloadsAdapter> iterator = loader.iterator();
        if (iterator.hasNext()) {
            jsonbConfig.withAdapters(iterator.next());
        }
        MAPPER = JsonbBuilder.create(jsonbConfig);
    }

    @Override
    public Class returnedClass() {
        return EventPayload.class;
    }

    @Override
    protected Jsonb mapper() {
        return MAPPER;
    }

}