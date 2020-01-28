package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;


import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Table(name = "EncryptedEventSecret")
@Entity
public class EncryptedEventSecretEntity implements EncryptedEventSecret {

    @EmbeddedId
    private EncryptedEventSecretId encryptedEventSecretId;

    @NotNull
    private Date creationDate;

    private String secret;

    @Override
    public String aggregateRootId() {
        return encryptedEventSecretId.aggregateRootId();
    }

    @Override
    public String aggregateRootType() {
        return encryptedEventSecretId.aggregateRootType();
    }

    @Override
    public Date creationDate() {
        return creationDate;
    }

    @Override
    public String secret() {
        return secret;
    }

    public EncryptedEventSecretEntity() {}

    public EncryptedEventSecretEntity(final EncryptedEventSecret encryptedEventSecret) {
        this(encryptedEventSecret.aggregateRootId(),
                encryptedEventSecret.aggregateRootType(),
                encryptedEventSecret.creationDate(),
                encryptedEventSecret.secret());
    }

    public EncryptedEventSecretEntity(final String aggregateRootId,
                                      final String aggregateRootType,
                                      final Date creationDate,
                                      final String secret) {
        this.encryptedEventSecretId = new EncryptedEventSecretId(aggregateRootId, aggregateRootType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncryptedEventSecretEntity)) return false;
        EncryptedEventSecretEntity that = (EncryptedEventSecretEntity) o;
        return Objects.equals(encryptedEventSecretId, that.encryptedEventSecretId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedEventSecretId);
    }

    @Override
    public String toString() {
        return "EncryptedEventSecretEntity{" +
                "encryptedEventSecretId=" + encryptedEventSecretId +
                ", creationDate=" + creationDate +
                ", secret='" + secret + '\'' +
                '}';
    }
}
