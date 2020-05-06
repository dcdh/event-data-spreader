package com.damdamdeo.eventdataspreader.queryside;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.EventConsumedEntity;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @BeforeEach
    @Transactional
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SECRET_STORE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
            return eventConsumedEntities.stream()
                    .filter(eventConsumedEntity -> eventConsumedEntity.consumed())
                    .count() == 3;
        });
        transaction.begin();
        final GiftEntity giftEntity = entityManager.find(GiftEntity.class, "MotorolaG6");
        final AccountEntity accountEntity = entityManager.find(AccountEntity.class, "damdamdeo");
        transaction.commit();

        assertEquals("MotorolaG6", giftEntity.name());
        assertEquals("toto", giftEntity.offeredTo());
        assertEquals(1l, giftEntity.version());

        assertEquals("damdamdeo", accountEntity.owner());
        assertEquals(new BigDecimal("900.00"), accountEntity.balance());
        assertEquals(0l, accountEntity.version());
    }

}
