package com.damdamdeo.eventdataspreader.writeside.debeziumeventconsumer.api;

public interface EventConsumer {

    void consume(Event event);

}
