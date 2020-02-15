package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventId;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EncryptedIdEventEntity implements EventId, Serializable {

    @Column(name="aggregateRootId")
    private String aggregateRootId;

    @Column(name="aggregateRootType")
    private String aggregateRootType;

    @Column(name="version")
    private Long version;

    public EncryptedIdEventEntity() {}

    public EncryptedIdEventEntity(final String aggregateRootId,
                                  final String aggregateRootType,
                                  final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

    public EncryptedIdEventEntity(final EventId eventId) {
        this(eventId.aggregateRootId(), eventId.aggregateRootType(), eventId.version());
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncryptedIdEventEntity)) return false;
        EncryptedIdEventEntity that = (EncryptedIdEventEntity) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, version);
    }

    @Override
    public String toString() {
        return "EncryptedIdEventEntity{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                '}';
    }
}
