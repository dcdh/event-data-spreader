package com.damdamdeo.eventsourced.mutable.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import java.time.LocalDateTime;
import java.util.Objects;

public final class EncryptedAggregateRootEvent<EVENT_PAYLOAD> {

    private final AggregateRootEventId aggregateRootEventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final EVENT_PAYLOAD eventPayload;

    public EncryptedAggregateRootEvent(final AggregateRootEventId aggregateRootEventId,
                                       final String eventType,
                                       final LocalDateTime creationDate,
                                       final EVENT_PAYLOAD eventPayload) {
        this.aggregateRootEventId = Objects.requireNonNull(aggregateRootEventId);
        this.eventType = Objects.requireNonNull(eventType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventPayload = Objects.requireNonNull(eventPayload);
    }

    public AggregateRootEventId aggregateRootEventId() {
        return aggregateRootEventId;
    }

    public String eventType() {
        return eventType;
    }

    public LocalDateTime creationDate() {
        return creationDate;
    }

    public EVENT_PAYLOAD eventPayload() {
        return eventPayload;
    }

    public EVENT_PAYLOAD decryptPayload(final CryptoService<?, EVENT_PAYLOAD> cryptoService) {
        return cryptoService.recursiveDecrypt(eventPayload);
    }

}
