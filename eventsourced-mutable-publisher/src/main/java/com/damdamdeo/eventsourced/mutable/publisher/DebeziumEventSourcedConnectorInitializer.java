package com.damdamdeo.eventsourced.mutable.publisher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.damdamdeo.eventsourced.mutable.publisher.dto.DebeziumConnectorConfigurationDTO;
import io.quarkus.runtime.StartupEvent;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DebeziumEventSourcedConnectorInitializer {

    private final static String EVENTSOURCED_CONNECTOR = "event-sourced-connector";

    private final Logger LOGGER = LoggerFactory.getLogger(DebeziumEventSourcedConnectorInitializer.class);

    private final DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator;

    private final KafkaConnectorApi kafkaConnectorApi;

    public DebeziumEventSourcedConnectorInitializer(@RestClient final KafkaConnectorApi kafkaConnectorApi,
                                                    final DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator) {
        this.kafkaConnectorApi = Objects.requireNonNull(kafkaConnectorApi);
        this.debeziumConnectorConfigurationGenerator = Objects.requireNonNull(debeziumConnectorConfigurationGenerator);
    }

    public void onStart(@Observes final StartupEvent ev) {
        final DebeziumConnectorConfigurationDTO connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration(EVENTSOURCED_CONNECTOR);
        final List<String> connectors = kafkaConnectorApi.getAllConnectors();
        if (!connectors.contains(EVENTSOURCED_CONNECTOR)) {
            kafkaConnectorApi.registerConnector(connectorConfiguration);
            LOGGER.info(String.format("Debezium connector registered using this configuration '%s'", connectorConfiguration.toString()));
        }
        final RetryPolicy<KafkaConnectorStatus> retryPolicy = new RetryPolicy<KafkaConnectorStatus>()
                .handleResultIf(kafkaConnectorStatus -> !kafkaConnectorStatus.isRunning())
                .withDelay(Duration.ofSeconds(1))
                .withMaxRetries(100)
                .onFailedAttempt(e -> LOGGER.error(String.format("Connector running attempt failed - connector current state '%s' - attempt count '%d'", e.getLastResult().state(), e.getAttemptCount())))
                .onRetry(e -> LOGGER.warn(String.format("Connector not running yet - connector current state '%s' - attempt count '%d'", e.getLastResult().state(), e.getAttemptCount())))
                .onRetriesExceeded(e -> LOGGER.warn("Failed for connector to run - Max retries exceeded - connector current state '%s' - attempt count '%d'", String.format(e.getResult().state(), e.getAttemptCount())))
                .onAbort(e -> LOGGER.warn("Wait for connector running state aborted - connector current state '%s' - attempt count '%d'", String.format(e.getResult().state(), e.getAttemptCount())));
        Failsafe.with(retryPolicy).run(() -> kafkaConnectorApi.connectorStatus(EVENTSOURCED_CONNECTOR));
    }

}
