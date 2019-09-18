package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Iterator;
import java.util.ServiceLoader;

public class JSONBAggregateRootUserType extends AbstractUserType {

    public static final JSONBAggregateRootUserType INSTANCE = new JSONBAggregateRootUserType();

    private static final Jsonb MAPPER;

    static {
        final JsonbConfig jsonbConfig = new JsonbConfig().withFormatting(true);
        final ServiceLoader<AggregateRootAdapter> loader = ServiceLoader.load(AggregateRootAdapter.class);
        final Iterator<AggregateRootAdapter> iterator = loader.iterator();
        if (iterator.hasNext()) {
            jsonbConfig.withAdapters(iterator.next());
        }
        MAPPER = JsonbBuilder.create(jsonbConfig);
    }

    @Override
    public Class returnedClass() {
        return AggregateRoot.class;
    }

    @Override
    protected Jsonb mapper() {
        return MAPPER;
    }

}