package com.damdamdeo.eventsourced.mutable.publisher;

import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class QuteDebeziumConnectorConfigurationGenerator implements DebeziumConnectorConfigurationGenerator {

    final Template debeziumTemplate;
    final String mutableHostname;
    final String mutableUsername;
    final String mutablePassword;
    final Integer mutablePort;
    final String mutableDbname;
    final Boolean slotDropOnStop;

    public QuteDebeziumConnectorConfigurationGenerator(@ResourcePath("debezium.json") final Template debeziumTemplate,
                                                       @ConfigProperty(name = "connector.mutable.database.hostname") final String mutableHostname,
                                                       @ConfigProperty(name = "connector.mutable.database.username") final String mutableUsername,
                                                       @ConfigProperty(name = "connector.mutable.database.password") final String mutablePassword,
                                                       @ConfigProperty(name = "connector.mutable.database.port") final Integer mutablePort,
                                                       @ConfigProperty(name = "connector.mutable.database.dbname") final String mutableDbname,
                                                       @ConfigProperty(name = "slot.drop.on.stop") final Boolean slotDropOnStop) {
        this.debeziumTemplate = Objects.requireNonNull(debeziumTemplate);
        this.mutableHostname = Objects.requireNonNull(mutableHostname);
        this.mutableUsername = Objects.requireNonNull(mutableUsername);
        this.mutablePassword = Objects.requireNonNull(mutablePassword);
        this.mutablePort = Objects.requireNonNull(mutablePort);
        this.mutableDbname = Objects.requireNonNull(mutableDbname);
        this.slotDropOnStop = slotDropOnStop == null ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public String generateConnectorConfiguration() {
        return this.debeziumTemplate.data("databaseHostname", mutableHostname)
                .data("name", DebeziumConnectorConfigurationGenerator.EVENTSOURCED_CONNECTOR)
                .data("databaseServerName", mutableHostname)
                .data("databaseDbname", mutableDbname)
                .data("databasePort", mutablePort)
                .data("databaseUser", this.mutableUsername)
                .data("databasePassword", this.mutablePassword)
                .data("slotDropOnStop", this.slotDropOnStop)
                .render();
    }

}
