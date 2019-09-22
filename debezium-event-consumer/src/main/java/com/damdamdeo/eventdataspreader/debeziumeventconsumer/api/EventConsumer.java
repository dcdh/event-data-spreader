package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

public interface EventConsumer {

    void consume(Event event);

}
