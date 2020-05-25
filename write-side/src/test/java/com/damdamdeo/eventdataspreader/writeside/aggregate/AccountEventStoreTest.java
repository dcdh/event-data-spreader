package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.metadata.UserAggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.AccountAggregateRootAccountDebitedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEvent;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AccountEventStoreTest extends AbstractTest {

    @Inject
    AggregateRootRepository aggregateRootRepository;

    @Inject
    EventRepository eventRepository;

    @Test
    public void should_debit_account() {
        // Given
        final AccountAggregateRoot accountAggregateRoot = new AccountAggregateRoot();
        accountAggregateRoot.handle(new DebitAccountCommand("owner", new BigDecimal("100.01"), "executedBy"));

        // When save
        final AccountAggregateRoot accountAggregateRootSaved = aggregateRootRepository.save(accountAggregateRoot);

        // Then
        assertEquals(new AccountAggregateRoot("owner", "owner", new BigDecimal("899.99"), 0l), accountAggregateRootSaved);
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC("owner", "AccountAggregateRoot");
        assertEquals(1, aggregateRootEvents.size());
        // -- AccountDebited
        assertEquals("owner", aggregateRootEvents.get(0).aggregateRootId());
        assertEquals("AccountAggregateRoot", aggregateRootEvents.get(0).aggregateRootType());
        assertEquals("AccountAggregateRootAccountDebitedAggregateRootEventPayload", aggregateRootEvents.get(0).eventType());
        assertEquals(0L, aggregateRootEvents.get(0).version());
        assertNotNull(aggregateRootEvents.get(0).creationDate());
        assertEquals(new UserAggregateRootEventMetadata("executedBy"), aggregateRootEvents.get(0).eventMetaData());
        assertEquals(new AccountAggregateRootAccountDebitedAggregateRootEventPayload("owner", new BigDecimal("100.01"), new BigDecimal("899.99")), aggregateRootEvents.get(0).eventPayload());
    }

}
