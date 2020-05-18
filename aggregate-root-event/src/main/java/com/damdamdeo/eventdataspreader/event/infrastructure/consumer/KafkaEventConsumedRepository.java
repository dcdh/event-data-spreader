package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.EventConsumedRepository;

public interface KafkaEventConsumedRepository extends EventConsumedRepository<KafkaSource> {
}
