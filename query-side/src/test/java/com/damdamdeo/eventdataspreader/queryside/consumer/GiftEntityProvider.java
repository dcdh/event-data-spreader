package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GiftEntityProvider {

    public GiftEntity create() {
        return new GiftEntity();
    }

}
