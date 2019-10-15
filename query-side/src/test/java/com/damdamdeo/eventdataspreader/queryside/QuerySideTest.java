package com.damdamdeo.eventdataspreader.queryside;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.EventConsumedEntity;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.awaitility.Awaitility.await;

@QuarkusTest
public class QuerySideTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction transaction;

    @BeforeEach
    @Transactional
    public void setup() {
        entityManager.createQuery("DELETE FROM GiftEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();
    }

    @Test
    public void should_consume_events() throws Exception {
        // When
        kafkaDebeziumProducer.produce("event/GiftBought.json");
        kafkaDebeziumProducer.produce("event/GiftOffered.json");
        kafkaDebeziumProducer.produce("event/AccountDebited.json");

        // Then
        await().atMost(10, TimeUnit.SECONDS).until(() -> {
            transaction.begin();
            final List<EventConsumedEntity> eventConsumedEntities = entityManager.createQuery("SELECT e FROM EventConsumedEntity e LEFT JOIN FETCH e.eventConsumerEntities").getResultList();
            transaction.commit();
            return eventConsumedEntities.size() == 3;
        });
        transaction.begin();
        final GiftEntity giftEntity = entityManager.find(GiftEntity.class, "Motorola G6");
        final AccountEntity accountEntity = entityManager.find(AccountEntity.class, "damdamdeo");
        transaction.commit();

        assertEquals("Motorola G6", giftEntity.name());
        assertEquals("toto", giftEntity.offeredTo());
        assertEquals(1l, giftEntity.version());

        assertEquals("damdamdeo", accountEntity.owner());
        assertEquals(new BigDecimal("900.00"), accountEntity.balance());
        assertEquals(0l, accountEntity.version());
    }

}
