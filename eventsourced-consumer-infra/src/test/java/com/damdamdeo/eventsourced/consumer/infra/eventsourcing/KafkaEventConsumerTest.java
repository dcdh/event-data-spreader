package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.consumer.infra.UnsupportedCryptoService;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbAggregateRootEventId;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbAggregateRootId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class KafkaEventConsumerTest {

    @InjectSpy
    KafkaAggregateRootEventConsumedRepository spiedKafkaEventConsumedRepository;

    @InjectMock
    CreatedAtProvider mockedCreatedAtProvider;

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @InjectMock
    UnsupportedCryptoService jsonCryptoService;

    @InjectSpy
    AccountDebitedAggregateRootEventConsumer spiedAccountDebitedAggregateRootEventConsumer;

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @BeforeEach
    public void setup() {
        doReturn(LocalDateTime.of(1980,01,01,0,0,0,0)).when(mockedCreatedAtProvider).createdAt();
        doNothing().when(jsonCryptoService).decrypt(any(), any());
    }

    @BeforeEach
    @AfterEach
    public void flushConsumedEvent() {
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT_CONSUMER CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ApplicationScoped
    public static class AccountDebitedAggregateRootEventConsumer implements JsonObjectAggregateRootEventConsumer {

        @Override
        public void consume(final AggregateRootEventConsumable<JsonObject> aggregateRootEventConsumable) {
            // If it fail, an exception will be thrown. In this case, the consumer will fail and retry again
            assertEquals("damdamdeo", aggregateRootEventConsumable.eventMetaData().getString("executedBy"));

            assertEquals("damdamdeo", aggregateRootEventConsumable.eventPayload().getString("owner"));
            assertEquals("100.00", aggregateRootEventConsumable.eventPayload().getString("price"));
            assertEquals("900.00", aggregateRootEventConsumable.eventPayload().getString("balance"));

            assertEquals("damdamdeo", aggregateRootEventConsumable.materializedState().getString("aggregateRootId"));
            assertEquals(0L, aggregateRootEventConsumable.materializedState().getJsonNumber("version").longValueExact());
            assertEquals("AccountAggregateRoot", aggregateRootEventConsumable.materializedState().getString("aggregateRootType"));
            assertEquals("900.00", aggregateRootEventConsumable.materializedState().getString("balance"));
        }

        @Override
        public String aggregateRootType() {
            return "AccountAggregateRoot";
        }

        @Override
        public String eventType() {
            return "AccountDebited";
        }
    }

    @Test
    public void should_consume_event_when_event_has_not_been_consumed() throws Exception {
        // Given

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        waitForEventToBeConsumed();

        // Then
        // 1569174260987000 in nanoseconds converted to 1569174260987 in milliseconds == Sunday 22 September 2019 17:44:20.987
        final AggregateRootEventConsumable aggregateRootEventConsumer = new DecryptedAggregateRootEventConsumable(
                new DebeziumJsonbAggregateRootEventId(new DebeziumJsonbAggregateRootId("damdamdeo", "AccountAggregateRoot"), 0l),
                "AccountDebited",
                LocalDateTime.of(2019, Month.SEPTEMBER, 22, 17, 44, 20, 987000000),
                Json.createParser(new StringReader("{\"owner\": \"damdamdeo\", \"price\": \"100.00\", \"balance\": \"900.00\"}")).getObject(),
                Json.createParser(new StringReader("{\"executedBy\": \"damdamdeo\"}")).getObject(),
                Json.createParser(new StringReader("{\"aggregateRootId\": \"damdamdeo\", \"version\":0, \"aggregateRootType\": \"AccountAggregateRoot\", \"balance\": \"900.00\"}")).getObject()
        );
        verify(spiedAccountDebitedAggregateRootEventConsumer, times(1)).consume(aggregateRootEventConsumer);
        verify(spiedKafkaEventConsumedRepository, times(1)).hasFinishedConsumingEvent(any());
    }

    @Test
    public void should_add_event_as_consumed_after_consuming_it() throws Exception {
        // Given

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        waitForEventToBeConsumed();

        // Then
        verify(spiedKafkaEventConsumedRepository, times(1)).addEventConsumerConsumed(
                eq(new DebeziumJsonbAggregateRootEventId(new DebeziumJsonbAggregateRootId("damdamdeo", "AccountAggregateRoot"), 0l)),
                any(),// Got the proxy of spiedAccountDebitedAggregateRootEventConsumer !
                eq(LocalDateTime.of(1980,01,01,0,0,0,0)),
                any(IncomingKafkaRecordKafkaInfrastructureMetadata.class),// trop compliqué de tester sur l'offset car celui-ci change en fonction du nombre d'executions
                eq("3bc9898721c64c5d6d17724bf6ec1c715cca0f69"));
    }

    @Test
    public void should_mark_event_as_consumed_after_all_consumers_have_consuming_it() throws Exception {
        // Given

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        waitForEventToBeConsumed();

        // Then
        verify(spiedKafkaEventConsumedRepository, times(1)).markEventAsConsumed(
                eq(new DebeziumJsonbAggregateRootEventId(new DebeziumJsonbAggregateRootId("damdamdeo", "AccountAggregateRoot"), 0l)),
                eq(LocalDateTime.of(1980,01,01,0,0,0,0)),
                any(IncomingKafkaRecordKafkaInfrastructureMetadata.class)// trop compliqué de tester sur l'offset car celui-ci change en fonction du nombre d'executions
        );
    }

    @Test
    public void should_consume_event_only_once() throws Exception {
        // Given
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        waitForEventToBeConsumed();

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        waitForEventToBeConsumed();

        // Then
        verify(spiedAccountDebitedAggregateRootEventConsumer, times(1)).consume(any());
    }

    private void waitForEventToBeConsumed() {
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> {
                    try (final Connection con = consumedEventsDataSource.getConnection();
                         final Statement stmt = con.createStatement();
                         final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS count FROM CONSUMED_EVENT " +
                                 "WHERE aggregaterootid = 'damdamdeo' AND aggregateroottype = 'AccountAggregateRoot' AND version = 0")) {
                        resultSet.next();
                        return resultSet.getLong("count") > 0;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
