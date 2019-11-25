package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

public interface KafkaSource {

    Integer partition();

    String topic();

    Long offset();

}
