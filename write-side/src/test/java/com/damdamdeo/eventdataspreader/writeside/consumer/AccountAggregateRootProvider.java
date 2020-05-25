package com.damdamdeo.eventdataspreader.writeside.consumer;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRoot;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountAggregateRootProvider {

    public AccountAggregateRoot create() {
        return new AccountAggregateRoot();
    }

}
