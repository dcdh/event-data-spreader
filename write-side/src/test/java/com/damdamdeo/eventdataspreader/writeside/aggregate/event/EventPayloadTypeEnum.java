package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;

import javax.json.Json;
import javax.json.JsonObject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

public enum EventPayloadTypeEnum {

    GIFT_BOUGHT_GIFT_PAYLOAD("GiftAggregate", "GiftBoughtPayload") {

        @Override
        public JsonObject toJsonObject(final EventPayload eventPayload) {
            return Json.createObjectBuilder()
                    .add(PAYLOAD_TYPE, eventPayloadType())
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootType())
                    .add("name", ((GiftBoughtPayload) eventPayload).name())
                    .build();
        }

        @Override
        public EventPayload toEventPayload(final JsonObject eventPayload) {
            return new GiftBoughtPayload(eventPayload.getString("name"));
        }

    },

    GIFT_OFFERED_GIFT_PAYLOAD("GiftAggregate", "GiftOfferedPayload") {

        @Override
        public JsonObject toJsonObject(final EventPayload eventPayload) {
            return Json.createObjectBuilder()
                    .add(PAYLOAD_TYPE, eventPayloadType())
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootType())
                    .add("name", ((GiftOfferedPayload) eventPayload).name())
                    .add("offeredTo", ((GiftOfferedPayload) eventPayload).offeredTo())
                    .build();
        }

        @Override
        public EventPayload toEventPayload(final JsonObject eventPayload) {
            return new GiftOfferedPayload(eventPayload.getString("name"),
                    eventPayload.getString("offeredTo"));
        }

    },

    ACCOUNT_DEBITED_ACCOUNT_PAYLOAD("AccountAggregate", "AccountDebitedPayload") {

        @Override
        public JsonObject toJsonObject(final EventPayload eventPayload) {
            return Json.createObjectBuilder()
                    .add(PAYLOAD_TYPE, eventPayloadType())
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootType())
                    .add("owner", ((AccountDebitedPayload) eventPayload).owner())
                    .add("price", ((AccountDebitedPayload) eventPayload).price().toString())
                    .add("balance", ((AccountDebitedPayload) eventPayload).balance().toString())
                    .build();
        }

        @Override
        public EventPayload toEventPayload(final JsonObject eventPayload) {
            return new AccountDebitedPayload(eventPayload.getString("owner"),
                    new BigDecimal(eventPayload.getString("price")),
                    new BigDecimal(eventPayload.getString("balance")));
        }

    };

    public final static String AGGREGATE_ROOT_TYPE = "@aggregaterootType";
    public final static String PAYLOAD_TYPE = "@payloadType";

    private final String aggregateRootType;
    private final String eventPayloadType;

    EventPayloadTypeEnum(final String aggregateRootType,
                         final String eventPayloadType) {
        this.aggregateRootType = aggregateRootType;
        this.eventPayloadType = eventPayloadType;
    }

    public abstract JsonObject toJsonObject(EventPayload eventPayload);

    public abstract EventPayload toEventPayload(JsonObject eventPayload);

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public String eventPayloadType() {
        return eventPayloadType;
    }

    public static Optional<EventPayloadTypeEnum> from(final String aggregateRootType,
                                                      final String eventPayloadType) {
        return Stream.of(EventPayloadTypeEnum.values())
                .filter(eventPayloadTypeEnum -> eventPayloadTypeEnum.aggregateRootType.equals(aggregateRootType)
                        && eventPayloadTypeEnum.eventPayloadType.equals(eventPayloadType))
                .findFirst();
    }

}
