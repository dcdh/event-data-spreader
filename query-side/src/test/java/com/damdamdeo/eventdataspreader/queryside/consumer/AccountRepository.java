package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.queryside.infrastructure.AccountEntity;

public interface AccountRepository {

    void persist(AccountEntity accountEntity);

}
