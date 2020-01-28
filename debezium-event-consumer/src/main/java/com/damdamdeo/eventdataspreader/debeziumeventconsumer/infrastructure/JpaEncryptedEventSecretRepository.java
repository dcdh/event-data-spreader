package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EncryptedEventSecretRepository;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Dependent
public class JpaEncryptedEventSecretRepository implements EncryptedEventSecretRepository {

    final EntityManager entityManager;

    public JpaEncryptedEventSecretRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    @Transactional
    public EncryptedEventSecret find(final String aggregateRootId, final String aggregateRootType) {
        return Optional.ofNullable(this.entityManager.find(EncryptedEventSecretEntity.class, new EncryptedEventSecretId(aggregateRootId, aggregateRootType)))
                .map(EncryptedEventSecret.class::cast)
                .orElseGet(() -> new NoEncryptedEventSecret(aggregateRootId, aggregateRootType));
    }

    @Override
    @Transactional
    public EncryptedEventSecret merge(final EncryptedEventSecret encryptedEventSecret) {
        return entityManager.merge(new EncryptedEventSecretEntity(encryptedEventSecret));
    }

    // No Secret due to anonymisation :)
    static final class NoEncryptedEventSecret implements EncryptedEventSecret {

        private final String aggregateRootId;
        private final String aggregateRootType;

        public NoEncryptedEventSecret(final String aggregateRootId, final String aggregateRootType) {
            this.aggregateRootId = aggregateRootId;
            this.aggregateRootType = aggregateRootType;
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
        public Date creationDate() {
            return new Date(0L);
        }

        @Override
        public String secret() {
            return null;
        }

    }

}
