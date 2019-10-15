package com.damdamdeo.eventdataspreader.queryside.interfaces;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
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
        final String owner = event.payload().getString("owner");
        final BigDecimal balance = new BigDecimal(event.payload().getString("balance"));
        final Long version = event.version();
        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.onAccountDebited(owner, balance, version);
        entityManager.persist(accountEntity);
    }

}
