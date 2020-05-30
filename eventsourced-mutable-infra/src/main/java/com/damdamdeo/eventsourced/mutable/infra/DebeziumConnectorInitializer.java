package com.damdamdeo.eventsourced.mutable.infra;

import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Startup
@ApplicationScoped
public class DebeziumConnectorInitializer {

    private static final String EVENTSOURCED_CONNECTOR = "event-sourced-connector";

    final KafkaConnectorApi kafkaConnectorApi;
    final Template debeziumTemplate;
    final String mutableHostname;
    final String mutableUsername;
    final String mutablePassword;
    final Integer mutablePort;
    final String mutableDbname;
    final Boolean slotDropOnStop;

    public DebeziumConnectorInitializer(@RestClient final KafkaConnectorApi kafkaConnectorApi,
                                        @ResourcePath("debezium.json") final Template debeziumTemplate,
                                        @ConfigProperty(name = "connector.mutable.database.hostname") final String mutableHostname,
                                        @ConfigProperty(name = "connector.mutable.database.username") final String mutableUsername,
                                        @ConfigProperty(name = "connector.mutable.database.password") final String mutablePassword,
                                        @ConfigProperty(name = "connector.mutable.database.port") final Integer mutablePort,
                                        @ConfigProperty(name = "connector.mutable.database.dbname") final String mutableDbname,
                                        @ConfigProperty(name = "slot.drop.on.stop") final Boolean slotDropOnStop) {
        this.kafkaConnectorApi = Objects.requireNonNull(kafkaConnectorApi);
        this.debeziumTemplate = Objects.requireNonNull(debeziumTemplate);
        this.mutableHostname = Objects.requireNonNull(mutableHostname);
        this.mutableUsername = Objects.requireNonNull(mutableUsername);
        this.mutablePassword = Objects.requireNonNull(mutablePassword);
        this.mutablePort = Objects.requireNonNull(mutablePort);
        this.mutableDbname = Objects.requireNonNull(mutableDbname);
        this.slotDropOnStop = slotDropOnStop == null ? Boolean.FALSE : Boolean.TRUE;
    }

    @PostConstruct
    public void onInit() {
        final String connectorConfiguration = this.debeziumTemplate.data("databaseHostname", mutableHostname)
                .data("databaseServerName", mutableHostname)
                .data("databaseDbname", mutableDbname)
                .data("databasePort", mutablePort)
                .data("databaseUser", this.mutableUsername)
                .data("databasePassword", this.mutablePassword)
                .data("slotDropOnStop", this.slotDropOnStop)
                .render();
        final List<String> connectors = kafkaConnectorApi.getAllConnectors();
        if (!connectors.contains(EVENTSOURCED_CONNECTOR)) {
            kafkaConnectorApi.registerConnector(connectorConfiguration);
        }
    }

}
