package com.damdamdeo.eventdataspreader.writeside.eventsourcing.user.type;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types.AggregateRootAdapter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class GiftAggregateRootAdapter implements AggregateRootAdapter,
        JsonbAdapter<AggregateRoot, JsonObject> // Quick fix an issue with yasson
    {

    private final static String DISCRIMINATOR = "@class";

    @Override
    public JsonObject adaptToJson(final AggregateRoot aggregateRoot) throws Exception {
        final String aggregateRootTypeSimpleName = aggregateRoot.getClass().getSimpleName();
        switch (aggregateRootTypeSimpleName) {
            case "GiftAggregate":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, aggregateRootTypeSimpleName)
                        .add("aggregateRootId", aggregateRoot.aggregateRootId())
                        .add("name", ((GiftAggregate) aggregateRoot).name())
                        .add("offeredTo", ((GiftAggregate) aggregateRoot).offeredTo())
                        .add("version", aggregateRoot.version())
                        .build();
            default:
                throw new IllegalStateException("Unknown aggregate type : " + aggregateRootTypeSimpleName);
        }
    }

    @Override
    public AggregateRoot adaptFromJson(final JsonObject aggregateRoot) throws Exception {
        switch (aggregateRoot.getString(DISCRIMINATOR)) {
            case "GiftAggregate":
                return new GiftAggregate(
                        aggregateRoot.getString("aggregateRootId"),
                        aggregateRoot.getString("name"),
                        aggregateRoot.getString("offeredTo"),
                        aggregateRoot.getJsonNumber("version").longValue()
                );
            default:
                throw new IllegalStateException("Unknown type : " + aggregateRoot.getString(DISCRIMINATOR));
        }
    }

}
