package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftBought;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftOffered;
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
public class GiftEventStoreTest {

    @Inject
    GiftAggregateRepository giftAggregateRepository;

    @Inject
    EntityManager entityManager;

    @Inject
    EventRepository eventRepository;

    @BeforeEach
    @Transactional
    public void setup() {
        entityManager.createQuery("DELETE FROM EventEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AggregateRootProjectionEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();
    }

    @Test
    public void should_buy_and_offer_the_gift() {
        // Given
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.apply(new GiftBought("Motorola G6"), new DefaultEventMetadata("damdamdeo"));
        giftAggregate.apply(new GiftOffered("Motorola G6", "toto"), new DefaultEventMetadata("damdamdeo"));

        // When save
        final GiftAggregate giftAggregateSaved = giftAggregateRepository.save(giftAggregate);

        // Then
        assertEquals("Motorola G6", giftAggregateSaved.aggregateRootId());
        assertEquals("Motorola G6", giftAggregateSaved.name());
        assertEquals(1l, giftAggregateSaved.version());

        final List<Event> events = eventRepository.load("Motorola G6", "GiftAggregate");
        assertEquals(2, events.size());
        // -- GiftBought
        assertNotNull(events.get(0).eventId());
        assertEquals("Motorola G6", events.get(0).aggregateRootId());
        assertEquals("GiftAggregate", events.get(0).aggregateRootType());
        assertEquals("GiftBought", events.get(0).eventType());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(new DefaultEventMetadata("damdamdeo"), events.get(0).eventMetaData());
        assertEquals(new GiftBought("Motorola G6"), events.get(0).eventPayload());
        // -- GiftOffered
        assertNotNull(events.get(1).eventId());
        assertEquals("Motorola G6", events.get(1).aggregateRootId());
        assertEquals("GiftAggregate", events.get(1).aggregateRootType());
        assertEquals("GiftOffered", events.get(1).eventType());
        assertEquals(1L, events.get(1).version());
        assertNotNull(events.get(1).creationDate());
        assertEquals(new DefaultEventMetadata("damdamdeo"), events.get(1).eventMetaData());
        assertEquals(new GiftOffered("Motorola G6", "toto"), events.get(1).eventPayload());
    }

}
