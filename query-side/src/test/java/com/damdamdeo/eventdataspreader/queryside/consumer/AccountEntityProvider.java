package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountEntityProvider {

    public AccountEntity create() {
        return new AccountEntity();
    }

}
