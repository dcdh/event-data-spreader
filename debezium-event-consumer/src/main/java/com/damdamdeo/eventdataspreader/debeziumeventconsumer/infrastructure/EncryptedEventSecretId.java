package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EncryptedEventSecretId implements Serializable {

    @NotNull
    private String aggregateRootId;

    @NotNull
    private String aggregateRootType;

    public EncryptedEventSecretId() {}

    public EncryptedEventSecretId(final String aggregateRootId,
                                  final String aggregateRootType) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncryptedEventSecretId)) return false;
        EncryptedEventSecretId that = (EncryptedEventSecretId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType);
    }

    @Override
    public String toString() {
        return "EncryptedEventSecretId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                '}';
    }

}
