package com.damdamdeo.eventdataspreader.queryside.interfaces;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.eventdataspreader.queryside.event.AccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
@EventQualifier(aggregateRootType = "AccountAggregate", eventType = "AccountDebited")
public class AccountDebitedEventConsumer implements EventConsumer {

    final EntityManager entityManager;

    public AccountDebitedEventConsumer(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public void consume(final Event event) {
        final AccountAggregateAccountDebitedEventPayload accountAggregateAccountDebitedEventPayload = (AccountAggregateAccountDebitedEventPayload) event.eventPayload();
        final Long version = event.version();
        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.onAccountDebited(accountAggregateAccountDebitedEventPayload.owner(),
                accountAggregateAccountDebitedEventPayload.balance(), version);
        entityManager.persist(accountEntity);
    }

}
