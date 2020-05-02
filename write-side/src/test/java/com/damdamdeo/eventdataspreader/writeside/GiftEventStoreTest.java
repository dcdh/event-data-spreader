package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftOfferedEventPayload;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class GiftEventStoreTest {

    @Inject
    GiftAggregateRepository giftAggregateRepository;

    @Inject
    EntityManager entityManager;

    @Inject
    EventRepository eventRepository;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @BeforeEach
    @Transactional
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SecretStore");
        } catch (SQLException e) {
            // Do not throw an exception as the table is not present because the @PostConstruct in AgroalDataSourceSecretStore
            // has not be called yet... bug ?!?
            // throw new RuntimeException(e);
        }

        entityManager.createQuery("DELETE FROM EncryptedEventEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AggregateRootEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();
    }

    @Test
    public void should_buy_and_offer_the_gift() {
        // Given
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.handle(new BuyGiftCommand("Motorola G6","damdamdeo"));
        giftAggregate.handle(new OfferGiftCommand("Motorola G6", "toto","damdamdeo"));

        // When save
        final GiftAggregate giftAggregateSaved = giftAggregateRepository.save(giftAggregate);

        // Then
        assertEquals("Motorola G6", giftAggregateSaved.aggregateRootId());
        assertEquals("Motorola G6", giftAggregateSaved.name());
        assertEquals(1l, giftAggregateSaved.version());

        final List<Event> events = eventRepository.load("Motorola G6", "GiftAggregate");
        assertEquals(2, events.size());
        // -- GiftBought
        assertEquals("Motorola G6", events.get(0).aggregateRootId());
        assertEquals("GiftAggregate", events.get(0).aggregateRootType());
        assertEquals("GiftBought", events.get(0).eventType());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(new DefaultEventMetadata("damdamdeo"), events.get(0).eventMetaData());
        assertEquals(new GiftAggregateGiftBoughtEventPayload("Motorola G6"), events.get(0).eventPayload());
        // -- GiftOffered
        assertEquals("Motorola G6", events.get(1).aggregateRootId());
        assertEquals("GiftAggregate", events.get(1).aggregateRootType());
        assertEquals("GiftOffered", events.get(1).eventType());
        assertEquals(1L, events.get(1).version());
        assertNotNull(events.get(1).creationDate());
        assertEquals(new DefaultEventMetadata("damdamdeo"), events.get(1).eventMetaData());
        assertEquals(new GiftAggregateGiftOfferedEventPayload("Motorola G6", "toto"), events.get(1).eventPayload());
    }

}
