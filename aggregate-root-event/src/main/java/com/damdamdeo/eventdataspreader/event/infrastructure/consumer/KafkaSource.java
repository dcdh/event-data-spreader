package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.Source;

public interface KafkaSource extends Source {

    Integer partition();

    String topic();

    Long offset();

}
