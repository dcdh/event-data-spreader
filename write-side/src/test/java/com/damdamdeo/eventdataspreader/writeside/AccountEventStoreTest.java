package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountDebited;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class AccountEventStoreTest {

    @Inject
    AccountAggregateRepository accountAggregateRepository;

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
    public void should_debit_account() {
        // Given
        final AccountAggregate accountAggregate = new AccountAggregate();
        accountAggregate.apply(new AccountDebited("owner", new BigDecimal("100.01")),
                new DefaultEventMetadata("owner"));

        // When save
        final AccountAggregate accountAggregateSaved = accountAggregateRepository.save(accountAggregate);

        // Then
        assertEquals("owner", accountAggregateSaved.aggregateRootId());
        assertEquals("owner", accountAggregateSaved.owner());
        assertEquals(new BigDecimal("899.99"), accountAggregateSaved.balance());
        assertEquals(0l, accountAggregateSaved.version());

        final List<Event> events = eventRepository.load("owner", "AccountAggregate");
        assertEquals(1, events.size());
        // -- AccountDebited
        assertNotNull(events.get(0).eventId());
        assertEquals("owner", events.get(0).aggregateRootId());
        assertEquals("AccountAggregate", events.get(0).aggregateRootType());
        assertEquals("AccountDebited", events.get(0).eventType());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(new DefaultEventMetadata("owner"), events.get(0).eventMetaData());
        assertEquals(new AccountDebited("owner", new BigDecimal("100.01")), events.get(0).eventPayload());
    }

}
