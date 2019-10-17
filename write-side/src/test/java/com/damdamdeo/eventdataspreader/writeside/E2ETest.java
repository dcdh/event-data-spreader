package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.EventConsumedEntity;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.EventEntity;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class E2ETest {

    @Inject
    GiftAggregateRepository giftAggregateRepository;

    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction transaction;

    @BeforeEach
    @Transactional
    public void setup() throws IOException {
        entityManager.createQuery("DELETE FROM EventEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AggregateRootProjectionEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();

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
//                    .statusCode(201)
            ;
        }
    }

    @AfterEach
    public void teardown() {
        given().delete("http://localhost:8083/connectors/test-connector");
    }

    @Test
    public void should_buy_offer_the_gift_and_debit_account() throws Exception{
        // Given
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.handle(new BuyGiftCommand("Motorola G6","damdamdeo"));
        giftAggregate.handle(new OfferGiftCommand("Motorola G6", "toto","damdamdeo"));

        // When
        giftAggregateRepository.save(giftAggregate);

        // Then
        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            transaction.begin();
            final List<EventEntity> events = entityManager.createQuery("SELECT e FROM EventEntity e").getResultList();
            final List<EventConsumedEntity> eventConsumedEntities = entityManager.createQuery("SELECT e FROM EventConsumedEntity e  LEFT JOIN FETCH e.eventConsumerEntities").getResultList();
            transaction.commit();
            return events.size() == 3 && eventConsumedEntities.size() == 3;
        });
        transaction.begin();
        final List<EventEntity> events = entityManager.createQuery("SELECT e FROM EventEntity e").getResultList();

        // -- GiftBought
        assertEquals("GiftAggregate", events.get(0).aggregateRootType());
        assertEquals("GiftBought", events.get(0).eventType());
        // -- GiftOffered
        assertEquals("GiftAggregate", events.get(1).aggregateRootType());
        assertEquals("GiftOffered", events.get(1).eventType());
        // -- AccountDebited
        assertEquals("AccountAggregate", events.get(2).aggregateRootType());
        assertEquals("AccountDebited", events.get(2).eventType());

        final List<EventConsumedEntity> eventConsumedEntities = entityManager.createQuery("SELECT e FROM EventConsumedEntity e  LEFT JOIN FETCH e.eventConsumerEntities").getResultList();
        transaction.commit();

        eventConsumedEntities.forEach(eventConsumedEntity -> assertEquals(true, eventConsumedEntity.consumed()));

        assertEquals(events.get(0).eventId(), eventConsumedEntities.get(0).eventId());
        assertEquals(events.get(1).eventId(), eventConsumedEntities.get(1).eventId());
        assertEquals(events.get(2).eventId(), eventConsumedEntities.get(2).eventId());
    }

}
