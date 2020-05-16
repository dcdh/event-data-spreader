package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import com.damdamdeo.eventdataspreader.queryside.consumer.GiftRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import java.util.Objects;

@ApplicationScoped
public class JpaGiftRepository implements GiftRepository {

    final EntityManager entityManager;

    public JpaGiftRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public GiftEntity find(final String name) {
        return entityManager.find(GiftEntity.class, name);
    }

    @Override
    public void persist(final GiftEntity giftEntity) {
        entityManager.persist(giftEntity);
    }

}
