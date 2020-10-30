package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.mutable.publisher.dto.EventSourcedConnectorConfigurationDTO;

public interface DebeziumConnectorConfigurationGenerator {

    String EVENTSOURCED_CONNECTOR = "event-sourced-connector";

    EventSourcedConnectorConfigurationDTO generateConnectorConfiguration();

}
