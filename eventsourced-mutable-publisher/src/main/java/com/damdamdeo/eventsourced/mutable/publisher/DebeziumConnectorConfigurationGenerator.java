package com.damdamdeo.eventsourced.mutable.publisher;

public interface DebeziumConnectorConfigurationGenerator {

    String EVENTSOURCED_CONNECTOR = "event-sourced-connector";

    String generateConnectorConfiguration();

}
