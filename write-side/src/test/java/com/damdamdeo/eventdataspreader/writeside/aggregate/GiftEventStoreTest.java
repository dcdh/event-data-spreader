package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.metadata.UserAggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftOfferedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEvent;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class GiftEventStoreTest extends AbstractTest {

    @Inject
    AggregateRootRepository aggregateRootRepository;

    @Inject
    EventRepository eventRepository;

    @Test
    public void should_buy_and_offer_the_gift() {
        // Given
        final GiftAggregateRoot giftAggregateRoot = new GiftAggregateRoot();
        giftAggregateRoot.handle(new BuyGiftCommand("lapinou","damdamdeo"));
        giftAggregateRoot.handle(new OfferGiftCommand("lapinou", "toto","damdamdeo"));

        // When save
        final GiftAggregateRoot giftAggregateRootSaved = aggregateRootRepository.save(giftAggregateRoot);

        // Then
        assertEquals(new GiftAggregateRoot("lapinou", "lapinou", "toto", 1l), giftAggregateRootSaved);

        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC("lapinou", "GiftAggregateRoot");
        assertEquals(2, aggregateRootEvents.size());
        // -- GiftBought
        assertEquals("lapinou", aggregateRootEvents.get(0).aggregateRootId());
        assertEquals("GiftAggregateRoot", aggregateRootEvents.get(0).aggregateRootType());
        assertEquals("GiftAggregateRootGiftBoughtAggregateRootEventPayload", aggregateRootEvents.get(0).eventType());
        assertEquals(0L, aggregateRootEvents.get(0).version());
        assertNotNull(aggregateRootEvents.get(0).creationDate());
        assertEquals(new UserAggregateRootEventMetadata("damdamdeo"), aggregateRootEvents.get(0).eventMetaData());
        assertEquals(new GiftAggregateRootGiftBoughtAggregateRootEventPayload("lapinou"), aggregateRootEvents.get(0).eventPayload());
        // -- GiftOffered
        assertEquals("lapinou", aggregateRootEvents.get(1).aggregateRootId());
        assertEquals("GiftAggregateRoot", aggregateRootEvents.get(1).aggregateRootType());
        assertEquals("GiftAggregateRootGiftOfferedAggregateRootEventPayload", aggregateRootEvents.get(1).eventType());
        assertEquals(1L, aggregateRootEvents.get(1).version());
        assertNotNull(aggregateRootEvents.get(1).creationDate());
        assertEquals(new UserAggregateRootEventMetadata("damdamdeo"), aggregateRootEvents.get(1).eventMetaData());
        assertEquals(new GiftAggregateRootGiftOfferedAggregateRootEventPayload("lapinou", "toto"), aggregateRootEvents.get(1).eventPayload());
    }

}
