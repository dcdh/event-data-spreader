package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayloadIdentifier;

public final class DefaultEventPayloadIdentifier implements EventPayloadIdentifier {

    private final String aggregateRootId;
    private final String eventType;
    private final EventPayloadTypeEnum eventPayloadTypeEnun;

    public DefaultEventPayloadIdentifier(final String aggregateRootId,
                                         final String eventType,
                                         final EventPayloadTypeEnum eventPayloadTypeEnun) {
        this.aggregateRootId = aggregateRootId;
        this.eventType = eventType;
        this.eventPayloadTypeEnun = eventPayloadTypeEnun;
    }

    @Override
    public String aggregateRootType() {
        return eventPayloadTypeEnun.aggregateRootType();
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public String eventPayloadType() {
        return eventPayloadTypeEnun.eventPayloadType();
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

}
