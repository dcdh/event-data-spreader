package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.DefaultEventId;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.PostgreSQLDecryptableEvent;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
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
    EventMetadataDeserializer eventMetadataDeserializer;

    @Inject
    AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;

    @BeforeEach
    @Transactional
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

    @Test
    public void should_buy_offer_the_gift_and_debit_account() throws Exception{
        // Given
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.handle(new BuyGiftCommand("Motorola G6","damdamdeo"));
        giftAggregate.handle(new OfferGiftCommand("Motorola G6", "toto","damdamdeo"));

        // When
        aggregateRootRepository.save(giftAggregate);

        // Then
        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            final List<Event> events = loadOrderByCreationDateASC();
            final Long nbOfConsumedEvent;
            try (final Connection con = consumedEventsDataSource.getConnection();
                 final Statement stmt = con.createStatement();
                 final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS nbOfConsumedEvent FROM CONSUMED_EVENT e WHERE e.consumed = true")) {
                resultSet.next();
                nbOfConsumedEvent = resultSet.getLong("nbOfConsumedEvent");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return events.size() == 3 && nbOfConsumedEvent == 3;
        });
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
             final ResultSet resultSet = stmt.executeQuery("SELECT aggregaterootid, aggregateroottype, version, consumed FROM CONSUMED_EVENT")) {
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
        private final DefaultEventId eventId;
        private final Boolean consumed;

        public EventConsumed(final ResultSet resultSet) throws Exception {
            this.eventId = new DefaultEventId(
                    resultSet.getString("aggregaterootid"),
                    resultSet.getString("aggregateroottype"),
                    resultSet.getLong("version"));
            this.consumed = resultSet.getBoolean("consumed");
        }

        public DefaultEventId eventId() {
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
                        aggregateRootEventPayloadDeSerializer, eventMetadataDeserializer));
            }
            resultSet.close();
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
