package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftBought;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftOffered;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandlerExecutor;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class GiftCommandHandlersTest {

    @Inject
    EntityManager entityManager;

    @Inject
    CommandHandlerExecutor commandHandlerExecutor;

    @Inject
    EventRepository eventRepository;

    @BeforeEach
    @Transactional
    public void setup() {
        entityManager.createQuery("DELETE FROM EventEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AggregateRootProjectionEntity").executeUpdate();
    }

    @Test
    public void should_buy_and_offer_the_gift() throws Throwable {
        GiftAggregate giftAggregate;
        // When
        giftAggregate = (GiftAggregate) commandHandlerExecutor.execute(new BuyGiftCommand("Motorola G6", "damdamdeo")).get();

        // Then
        assertEquals("Motorola G6", giftAggregate.aggregateRootId());
        assertEquals("Motorola G6", giftAggregate.name());
        assertEquals(null, giftAggregate.offeredTo());
        assertEquals(0l, giftAggregate.version());

        // When
        giftAggregate = (GiftAggregate) commandHandlerExecutor.execute(new OfferGiftCommand("Motorola G6", "toto", "damdamdeo")).get();

        // Then
        assertEquals("Motorola G6", giftAggregate.aggregateRootId());
        assertEquals("Motorola G6", giftAggregate.name());
        assertEquals("toto", giftAggregate.offeredTo());
        assertEquals(1l, giftAggregate.version());

        final List<Event> events = eventRepository.load("Motorola G6", "GiftAggregate");
        assertEquals(2, events.size());
        // -- GiftBought
        assertNotNull(events.get(0).eventId());
        assertEquals("Motorola G6", events.get(0).aggregateRootId());
        assertEquals("GiftAggregate", events.get(0).aggregateRootType());
        assertEquals("GiftBought", events.get(0).eventType());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(new GiftEventMetadata("damdamdeo"), events.get(0).eventMetaData());
        assertEquals(new GiftBought("Motorola G6"), events.get(0).eventPayload());
        // -- GiftOffered
        assertNotNull(events.get(1).eventId());
        assertEquals("Motorola G6", events.get(1).aggregateRootId());
        assertEquals("GiftAggregate", events.get(1).aggregateRootType());
        assertEquals("GiftOffered", events.get(1).eventType());
        assertEquals(1L, events.get(1).version());
        assertNotNull(events.get(1).creationDate());
        assertEquals(new GiftEventMetadata("damdamdeo"), events.get(1).eventMetaData());
        assertEquals(new GiftOffered("Motorola G6", "toto"), events.get(1).eventPayload());
    }

}
