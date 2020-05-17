package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;

public interface KafkaEventConsumedRepository extends EventConsumedRepository<KafkaSource> {
}
