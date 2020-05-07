package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class AccountEventStoreTest extends AbstractTest {

    @Inject
    AccountAggregateRepository accountAggregateRepository;

    @Inject
    EventRepository eventRepository;

    @Test
    public void should_debit_account() {
        // Given
        final AccountAggregate accountAggregate = new AccountAggregate();
        accountAggregate.handle(new DebitAccountCommand("owner", new BigDecimal("100.01"), "executedBy"));

        // When save
        final AccountAggregate accountAggregateSaved = accountAggregateRepository.save(accountAggregate);

        // Then
        assertEquals("owner", accountAggregateSaved.aggregateRootId());
        assertEquals("owner", accountAggregateSaved.owner());
        assertEquals(new BigDecimal("899.99"), accountAggregateSaved.balance());
        assertEquals(0l, accountAggregateSaved.version());

        final List<Event> events = eventRepository.loadOrderByCreationDateASC("owner", "AccountAggregate");
        assertEquals(1, events.size());
        // -- AccountDebited
        assertEquals("owner", events.get(0).aggregateRootId());
        assertEquals("AccountAggregate", events.get(0).aggregateRootType());
        assertEquals("AccountDebited", events.get(0).eventType());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(new DefaultEventMetadata("executedBy"), events.get(0).eventMetaData());
        assertEquals(new AccountAggregateAccountDebitedEventPayload("owner", new BigDecimal("100.01"), new BigDecimal("899.99")), events.get(0).eventPayload());
    }

}
