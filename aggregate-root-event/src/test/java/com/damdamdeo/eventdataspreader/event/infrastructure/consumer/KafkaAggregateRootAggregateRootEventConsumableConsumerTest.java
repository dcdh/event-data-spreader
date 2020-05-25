package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.event.api.AggregateRootId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class KafkaAggregateRootAggregateRootEventConsumableConsumerTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @Inject
    KafkaAggregateRootEventConsumedRepository kafkaEventConsumedRepository;

    @BeforeEach
    public void setup() {
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT_CONSUMER CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class AccountDebitedAggregateRootEventId implements AggregateRootEventId {

        @Override
        public AggregateRootId aggregateRootId() {
            return new AggregateRootId() {

                @Override
                public String aggregateRootId() {
                    return "damdamdeo";
                }

                @Override
                public String aggregateRootType() {
                    return "AccountAggregateRoot";
                }

            };
        }

        @Override
        public Long version() {
            return 0l;
        }

    }

    @Test
    public void should_consume_event() throws Exception {
        // Given
        final AggregateRootEventId accountDebitedAggregateRootEventId = new AccountDebitedAggregateRootEventId();
        // When
        kafkaDebeziumProducer.produce("event/AccountDebited.json");

        // Then
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> kafkaEventConsumedRepository.hasFinishedConsumingEvent(accountDebitedAggregateRootEventId));

        final List<String> consumersHavingProcessedEvent = kafkaEventConsumedRepository.getConsumersHavingProcessedEvent(accountDebitedAggregateRootEventId);

        assertEquals(Collections.singletonList("com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.AccountDebitedEventConsumer"),
                consumersHavingProcessedEvent);
    }

}
