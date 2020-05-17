package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Source;

public interface KafkaSource extends Source {

    Integer partition();

    String topic();

    Long offset();

}
