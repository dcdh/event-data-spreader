package com.damdamdeo.eventdataspreader.writeside.user.type;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountDebited;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftBought;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftOffered;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types.EventPayloadsAdapter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;
import java.math.BigDecimal;

public class DefaultEventPayloadsAdapter implements EventPayloadsAdapter,
        JsonbAdapter<EventPayload, JsonObject> // Quick fix an issue with yasson
    {

    private final static String DISCRIMINATOR = "@class";
// TODO je devrais avoir des noms full qualifié !!!!
    @Override
    public JsonObject adaptToJson(final EventPayload eventPayload) {
        final String eventPayloadTypeSimpleName = eventPayload.getClass().getSimpleName();
        switch (eventPayloadTypeSimpleName) {
            case "GiftBought":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, eventPayloadTypeSimpleName)
                        .add("name", ((GiftBought) eventPayload).name())
                        .build();
            case "GiftOffered":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, eventPayloadTypeSimpleName)
                        .add("name", ((GiftOffered) eventPayload).name())
                        .add("offeredTo", ((GiftOffered) eventPayload).offeredTo())
                        .build();
            case "AccountDebited":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, eventPayloadTypeSimpleName)
                        .add("owner", ((AccountDebited) eventPayload).owner())
                        .add("price", ((AccountDebited) eventPayload).price().toString())
                        .add("balance", ((AccountDebited) eventPayload).balance().toString())
                        .build();
            default:
                throw new IllegalStateException("Unknown event type : " + eventPayloadTypeSimpleName);
        }
    }

    @Override
    public EventPayload adaptFromJson(final JsonObject eventPayload) {
        switch (eventPayload.getString(DISCRIMINATOR)) {
            case "GiftBought":
                return new GiftBought(eventPayload.getString("name"));
            case "GiftOffered":
                return new GiftOffered(eventPayload.getString("name"),
                        eventPayload.getString("offeredTo"));
            case "AccountDebited":
                return new AccountDebited(eventPayload.getString("owner"),
                        new BigDecimal(eventPayload.getString("price")),
                        new BigDecimal(eventPayload.getString("balance")));
            default:
                throw new IllegalStateException("Unknown event type : " + eventPayload.getString(DISCRIMINATOR));
        }
    }

}
