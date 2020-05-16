package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.queryside.event.AccountAggregateAccountDebitedEventPayload;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class AccountDebitedEventConsumer implements EventConsumer {

    final AccountRepository accountRepository;
    final AccountEntityProvider accountEntityProvider;

    public AccountDebitedEventConsumer(final AccountRepository accountRepository,
                                       final AccountEntityProvider accountEntityProvider) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.accountEntityProvider = Objects.requireNonNull(accountEntityProvider);
    }

    @Override
    public void consume(final Event event) {
        // TODO find a way to avoid casting
        final AccountAggregateAccountDebitedEventPayload accountAggregateAccountDebitedEventPayload = (AccountAggregateAccountDebitedEventPayload) event.eventPayload();
        final Long version = event.version();
        final AccountEntity accountEntity = this.accountEntityProvider.create();
        accountEntity.onAccountDebited(accountAggregateAccountDebitedEventPayload.owner(),
                accountAggregateAccountDebitedEventPayload.balance(), version);
        accountRepository.persist(accountEntity);
    }

    @Override
    public String aggregateRootType() {
        return "AccountAggregate";
    }

    @Override
    public String eventType() {
        return "AccountDebited";
    }

}
