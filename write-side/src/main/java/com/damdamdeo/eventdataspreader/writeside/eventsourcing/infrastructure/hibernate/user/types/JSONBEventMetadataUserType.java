package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventMetadata;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Iterator;
import java.util.ServiceLoader;

public class JSONBEventMetadataUserType extends AbstractUserType {

    public static final JSONBEventMetadataUserType INSTANCE = new JSONBEventMetadataUserType();

    private static final Jsonb MAPPER;

    static {
        final JsonbConfig jsonbConfig = new JsonbConfig().withFormatting(true);
        final ServiceLoader<EventMetadataAdapter> loader = ServiceLoader.load(EventMetadataAdapter.class);
        final Iterator<EventMetadataAdapter> iterator = loader.iterator();
        if (iterator.hasNext()) {
            jsonbConfig.withAdapters(iterator.next());
        }
        MAPPER = JsonbBuilder.create(jsonbConfig);
    }

    @Override
    protected Jsonb mapper() {
        return MAPPER;
    }

    @Override
    public Class returnedClass() {
        return EventMetadata.class;
    }
}