package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.mutable.publisher.dto.DebeziumConnectorConfigurationDTO;

public interface DebeziumConnectorConfigurationGenerator {

    DebeziumConnectorConfigurationDTO generateConnectorConfiguration(String connectorName);

}
