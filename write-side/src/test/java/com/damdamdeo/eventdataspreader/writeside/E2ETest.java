package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;
import com.damdamdeo.eventdataspreader.event.api.EventId;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.PostgreSQLDecryptableEvent;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.PostgreSQLEventId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class E2ETest {

    @Inject
    AggregateRootRepository aggregateRootRepository;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    @DataSource("aggregate-root-projection-event-store")
    AgroalDataSource aggregateRootProjectionEventStoreDataSource;

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @Inject
    EventMetadataDeSerializer eventMetadataDeSerializer;

    @Inject
    AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;

    @Inject
    EventConsumedRepository eventConsumedRepository;

    @BeforeEach
    public void setup() throws Exception {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SECRET_STORE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        given()
                .when()
                .delete("http://localhost:8083/connectors/test-connector");

        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE AGGREGATE_ROOT_PROJECTION");
            stmt.executeUpdate("TRUNCATE TABLE EVENT");
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

        final ClassLoader classLoader = getClass().getClassLoader();
        try (final InputStream inputStream = classLoader.getResourceAsStream("debezium.json")) {
            final String debezium = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            given()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(debezium)
                    .when()
                    .post("http://localhost:8083/connectors/")
                    .then()
                    .log()
                    .all()
                    .statusCode(201)
            ;
        }
        await().atMost(30, TimeUnit.SECONDS).until(() -> given()
                .get("http://localhost:8083/connectors/test-connector/status")
                .then().log().all()
                .extract()
                .body().jsonPath().getList("tasks").isEmpty() == false);
        await().atMost(30, TimeUnit.SECONDS).until(() -> given()
                .get("http://localhost:8083/connectors/test-connector/status")
                .then().log().all()
                .extract()
                .body().jsonPath().getString("tasks[0].state").equals("RUNNING"));
        Thread.sleep(1000);
    }

    @AfterEach
    public void teardown() {
        given()
                .when()
                .delete("http://localhost:8083/connectors/test-connector")
                .then()
                .statusCode(204);
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
    public void should_buy_offer_the_gift_and_debit_account() throws Exception{
        // Given
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.handle(new BuyGiftCommand("Motorola G6","damdamdeo"));
        giftAggregate.handle(new OfferGiftCommand("Motorola G6", "toto","damdamdeo"));

        // When
        aggregateRootRepository.save(giftAggregate);

        // Then
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> eventConsumedRepository.hasFinishedConsumingEvent(
                        new TestEventId("Motorola G6", "GiftAggregate", 0l)));
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> eventConsumedRepository.hasFinishedConsumingEvent(
                        new TestEventId("Motorola G6", "GiftAggregate", 1l)));
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> eventConsumedRepository.hasFinishedConsumingEvent(
                        new TestEventId("damdamdeo", "AccountAggregate", 0l)));

        final List<Event> events = loadOrderByCreationDateASC();

        // -- GiftBought
        assertEquals("GiftAggregate", events.get(0).aggregateRootType());
        assertEquals("GiftBought", events.get(0).eventType());
        // -- GiftOffered
        assertEquals("GiftAggregate", events.get(1).aggregateRootType());
        assertEquals("GiftOffered", events.get(1).eventType());
        // -- AccountDebited
        assertEquals("AccountAggregate", events.get(2).aggregateRootType());
        assertEquals("AccountDebited", events.get(2).eventType());

        final List<EventConsumed> eventsConsumed = new ArrayList<>();
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT aggregaterootid, aggregateroottype, version, consumed FROM CONSUMED_EVENT ORDER BY kafkaoffset ASC")) {
            while (resultSet.next()) {
                eventsConsumed.add(new EventConsumed(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        eventsConsumed.forEach(eventConsumedEntity -> assertEquals(true, eventConsumedEntity.consumed(), "Event not consumed " + eventConsumedEntity.toString()));

        assertEquals(events.get(0).eventId(), eventsConsumed.get(0).eventId());
        assertEquals(events.get(1).eventId(), eventsConsumed.get(1).eventId());
        assertEquals(events.get(2).eventId(), eventsConsumed.get(2).eventId());
    }

    private static final class EventConsumed {
        private final PostgreSQLEventId eventId;
        private final Boolean consumed;

        public EventConsumed(final ResultSet resultSet) throws Exception {
            this.eventId = new PostgreSQLEventId(resultSet);
            this.consumed = resultSet.getBoolean("consumed");
        }

        public PostgreSQLEventId eventId() {
            return eventId;
        }

        public Boolean consumed() {
            return consumed;
        }

    }

    private List<Event> loadOrderByCreationDateASC() {
        try (final Connection connection = aggregateRootProjectionEventStoreDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e ORDER BY e.creationdate ASC")) {
            final ResultSet resultSet = stmt.executeQuery();
            // TODO I should get the number of event to initialize list size. However, a lot of copies will be made in memory on large result set.
            final List<Event> events = new ArrayList<>();
            while (resultSet.next()) {
                events.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(Optional.empty(),
                        aggregateRootEventPayloadDeSerializer, eventMetadataDeSerializer));
            }
            resultSet.close();
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
