package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GiftAggregateRootProvider {

    public GiftAggregate create() {
        return new GiftAggregate();
    }

}
