package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.InfrastructureMetadata;

public interface KafkaInfrastructureMetadata extends InfrastructureMetadata {

    Integer partition();

    String topic();

    Long offset();

}
