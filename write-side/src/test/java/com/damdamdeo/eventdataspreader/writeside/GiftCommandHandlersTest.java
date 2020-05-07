package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftOfferedEventPayload;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.handler.BuyGiftCommandHandler;
import com.damdamdeo.eventdataspreader.writeside.command.handler.OfferGiftCommandHandler;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class GiftCommandHandlersTest extends AbstractTest {

    @Inject
    BuyGiftCommandHandler buyGiftCommandHandler;

    @Inject
    OfferGiftCommandHandler offerGiftCommandHandler;

    @Inject
    EventRepository eventRepository;

    @Test
    public void should_buy_and_offer_the_gift() throws Throwable {
        GiftAggregate giftAggregate;
        // When
        giftAggregate = buyGiftCommandHandler.executeCommand(new BuyGiftCommand("Motorola G6", "damdamdeo"));

        // Then
        assertEquals("Motorola G6", giftAggregate.aggregateRootId());
        assertEquals("Motorola G6", giftAggregate.name());
        assertEquals(null, giftAggregate.offeredTo());
        assertEquals(0l, giftAggregate.version());

        // When
        giftAggregate = offerGiftCommandHandler.executeCommand(new OfferGiftCommand("Motorola G6", "toto", "damdamdeo"));

        // Thenexecute
        assertEquals("Motorola G6", giftAggregate.aggregateRootId());
        assertEquals("Motorola G6", giftAggregate.name());
        assertEquals("toto", giftAggregate.offeredTo());
        assertEquals(1l, giftAggregate.version());

        final List<Event> events = eventRepository.loadOrderByCreationDateASC("Motorola G6", "GiftAggregate");
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
