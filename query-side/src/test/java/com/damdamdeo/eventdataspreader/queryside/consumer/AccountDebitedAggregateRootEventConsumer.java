package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumer;
import com.damdamdeo.eventdataspreader.queryside.consumer.payload.AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class AccountDebitedAggregateRootEventConsumer implements AggregateRootEventConsumer {

    final AccountRepository accountRepository;
    final AccountEntityProvider accountEntityProvider;

    public AccountDebitedAggregateRootEventConsumer(final AccountRepository accountRepository,
                                                    final AccountEntityProvider accountEntityProvider) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.accountEntityProvider = Objects.requireNonNull(accountEntityProvider);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        // TODO find a way to avoid casting
        final AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer accountAggregateAccountDebitedEventPayload = (AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        final AccountEntity accountEntity = this.accountEntityProvider.create();
        accountEntity.onAccountDebited(accountAggregateAccountDebitedEventPayload.owner(),
                accountAggregateAccountDebitedEventPayload.balance(), aggregateRootEventConsumable.eventId());
        accountRepository.persist(accountEntity);
    }

    @Override
    public String aggregateRootType() {
        return "AccountAggregateRoot";
    }

    @Override
    public String eventType() {
        return "AccountDebited";
    }

}
