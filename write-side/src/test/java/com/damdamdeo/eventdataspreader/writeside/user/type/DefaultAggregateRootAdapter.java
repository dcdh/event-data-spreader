package com.damdamdeo.eventdataspreader.writeside.user.type;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types.AggregateRootAdapter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;
import java.math.BigDecimal;
import java.util.Optional;

public class DefaultAggregateRootAdapter implements AggregateRootAdapter,
        JsonbAdapter<AggregateRoot, JsonObject> // Quick fix an issue with yasson
    {

    private final static String AGGREGATE_ROOT_TYPE = "@aggregaterootType";

    private static final String GIFT_AGGREGATE = "GiftAggregate";
    private static final String ACCOUNT_AGGREGATE = "AccountAggregate";

    private static final String AGGREGATE_ROOT_ID = "aggregateRootId";
    private static final String NAME = "name";
    private static final String OFFERED_TO = "offeredTo";
    private static final String OWNER = "owner";
    private static final String BALANCE = "balance";
    private static final String VERSION = "version";

    @Override
    public JsonObject adaptToJson(final AggregateRoot aggregateRoot) throws Exception {
        final String aggregateRootTypeSimpleName = aggregateRoot.getClass().getSimpleName();
        switch (aggregateRootTypeSimpleName) {
            case GIFT_AGGREGATE:
                return Json.createObjectBuilder()
                        .add(AGGREGATE_ROOT_TYPE, aggregateRootTypeSimpleName)
                        .add(AGGREGATE_ROOT_ID, aggregateRoot.aggregateRootId())
                        .add(NAME, ((GiftAggregate) aggregateRoot).name())
                        .add(OFFERED_TO, Optional.ofNullable(((GiftAggregate) aggregateRoot).offeredTo()).orElse(""))
                        .add(VERSION, aggregateRoot.version())
                        .build();
            case ACCOUNT_AGGREGATE:
                return Json.createObjectBuilder()
                        .add(AGGREGATE_ROOT_TYPE, aggregateRootTypeSimpleName)
                        .add(AGGREGATE_ROOT_ID, aggregateRoot.aggregateRootId())
                        .add(OWNER, ((AccountAggregate) aggregateRoot).owner())
                        .add(BALANCE, ((AccountAggregate) aggregateRoot).balance().toString())
                        .add(VERSION, aggregateRoot.version())
                        .build();
            default:
                throw new IllegalStateException("Unknown aggregate type : " + aggregateRootTypeSimpleName);
        }
    }

    @Override
    public AggregateRoot adaptFromJson(final JsonObject aggregateRoot) throws Exception {
        switch (aggregateRoot.getString(AGGREGATE_ROOT_TYPE)) {
            case GIFT_AGGREGATE:
                return new GiftAggregate(
                        aggregateRoot.getString(AGGREGATE_ROOT_ID),
                        aggregateRoot.getString(NAME),
                        aggregateRoot.getString(OFFERED_TO),
                        aggregateRoot.getJsonNumber(VERSION).longValue()
                );
            case ACCOUNT_AGGREGATE:
                return new AccountAggregate(
                        aggregateRoot.getString(AGGREGATE_ROOT_ID),
                        aggregateRoot.getString(OWNER),
                        new BigDecimal(aggregateRoot.getString(BALANCE)),
                        aggregateRoot.getJsonNumber(VERSION).longValue()
                );
            default:
                throw new IllegalStateException("Unknown type : " + aggregateRoot.getString(AGGREGATE_ROOT_TYPE));
        }
    }

}
