package com.damdamdeo.eventdataspreader.queryside.event;

import com.damdamdeo.eventdataspreader.event.api.EventPayload;

public interface GiftAggregateGiftOfferedEventPayload extends EventPayload {

    String name();

    String offeredTo();

}
