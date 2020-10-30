package com.damdamdeo.eventsourced.mutable.publisher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.damdamdeo.eventsourced.mutable.publisher.dto.EventSourcedConnectorConfigurationDTO;
import io.quarkus.runtime.StartupEvent;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DebeziumConnectorInitializer {

    private final Logger LOGGER = LoggerFactory.getLogger(DebeziumConnectorInitializer.class);

    private final DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator;

    private final KafkaConnectorApi kafkaConnectorApi;

    public DebeziumConnectorInitializer(@RestClient final KafkaConnectorApi kafkaConnectorApi,
                                        final DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator) {
        this.kafkaConnectorApi = Objects.requireNonNull(kafkaConnectorApi);
        this.debeziumConnectorConfigurationGenerator = Objects.requireNonNull(debeziumConnectorConfigurationGenerator);
    }

    public void onStart(@Observes final StartupEvent ev) {
        final EventSourcedConnectorConfigurationDTO connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration();
        final List<String> connectors = kafkaConnectorApi.getAllConnectors();
        if (!connectors.contains(DebeziumConnectorConfigurationGenerator.EVENTSOURCED_CONNECTOR)) {
            kafkaConnectorApi.registerConnector(connectorConfiguration);
            LOGGER.info(String.format("Debezium connector registered using this configuration '%s'", connectorConfiguration.toString()));
        }
        final RetryPolicy<KafkaConnectorStatus> retryPolicy = new RetryPolicy<KafkaConnectorStatus>()
                .handleResultIf(kafkaConnectorStatus -> !kafkaConnectorStatus.isRunning())
                .withDelay(Duration.ofSeconds(1))
                .withMaxRetries(100);
        Failsafe.with(retryPolicy).run(() -> kafkaConnectorApi.connectorStatus(DebeziumConnectorConfigurationGenerator.EVENTSOURCED_CONNECTOR));
    }

}
