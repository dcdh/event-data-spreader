package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GiftAggregateRootProvider {

    public GiftAggregateRoot create() {
        return new GiftAggregateRoot();
    }

}
