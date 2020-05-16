package com.damdamdeo.eventdataspreader.queryside.consumer;

import com.damdamdeo.eventdataspreader.queryside.infrastructure.GiftEntity;

public interface GiftRepository {

    GiftEntity find(String name);

    void persist(GiftEntity giftEntity);

}
