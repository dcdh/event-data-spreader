package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.InfrastructureMetadata;

public interface KafkaInfrastructureMetadata extends InfrastructureMetadata {

    Integer partition();

    String topic();

    Long offset();

}
