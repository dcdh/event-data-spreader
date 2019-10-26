package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

public class UnableToDecodeDebeziumEventMessageException extends Exception {

    public UnableToDecodeDebeziumEventMessageException(final String message) {
        super(message);
    }

}
