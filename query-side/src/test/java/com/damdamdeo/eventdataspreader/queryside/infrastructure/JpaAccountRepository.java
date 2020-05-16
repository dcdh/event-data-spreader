package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import com.damdamdeo.eventdataspreader.queryside.consumer.AccountRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import java.util.Objects;

@ApplicationScoped
public class JpaAccountRepository implements AccountRepository {

    final EntityManager entityManager;

    public JpaAccountRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public void persist(final AccountEntity accountEntity) {
        this.entityManager.persist(accountEntity);
    }

}
