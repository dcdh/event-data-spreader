package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.event.api.EventPayload;

import java.math.BigDecimal;

public interface AccountAggregateAccountDebitedEventPayload extends EventPayload {

    String owner();

    BigDecimal balance();


}
