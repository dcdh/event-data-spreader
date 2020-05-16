package com.damdamdeo.eventdataspreader.queryside;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;
import com.damdamdeo.eventdataspreader.event.api.EventId;
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

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @Inject
    EventConsumedRepository eventConsumedRepository;

    @BeforeEach
    @Transactional
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SECRET_STORE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT_CONSUMER CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        entityManager.createQuery("DELETE FROM GiftEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountEntity").executeUpdate();
    }

    private static final class TestEventId implements EventId {

        private final String aggregateRootId;
        private final String aggregateRootType;
        private final Long version;

        public TestEventId(final String aggregateRootId, final String aggregateRootType, final Long version) {
            this.aggregateRootId = aggregateRootId;
            this.aggregateRootType = aggregateRootType;
            this.version = version;
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
    }

    @Test
    public void should_consume_events() throws Exception {
        // When
        kafkaDebeziumProducer.produce("event/GiftBought.json");
        kafkaDebeziumProducer.produce("event/GiftOffered.json");
        kafkaDebeziumProducer.produce("event/AccountDebited.json");

        // Then
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> eventConsumedRepository.hasFinishedConsumingEvent(
                        new TestEventId("MotorolaG6", "GiftAggregate", 0l)));
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> eventConsumedRepository.hasFinishedConsumingEvent(
                        new TestEventId("MotorolaG6", "GiftAggregate", 1l)));
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> eventConsumedRepository.hasFinishedConsumingEvent(
                        new TestEventId("damdamdeo", "AccountAggregate", 0l)));

        transaction.begin();
        final GiftEntity giftEntity = entityManager.find(GiftEntity.class, "MotorolaG6");
        final AccountEntity accountEntity = entityManager.find(AccountEntity.class, "damdamdeo");
        transaction.commit();
//FCK
        assertEquals("MotorolaG6", giftEntity.name());
        assertEquals("toto", giftEntity.offeredTo());
        assertEquals(1l, giftEntity.version());

        assertEquals("damdamdeo", accountEntity.owner());
        assertEquals(new BigDecimal("900.00"), accountEntity.balance());
        assertEquals(0l, accountEntity.version());
    }

}
