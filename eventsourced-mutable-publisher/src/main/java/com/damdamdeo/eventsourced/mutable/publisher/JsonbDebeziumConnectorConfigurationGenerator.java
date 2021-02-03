package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.mutable.publisher.dto.DebeziumConnectorConfigurationConfigDTO;
import com.damdamdeo.eventsourced.mutable.publisher.dto.DebeziumConnectorConfigurationDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JsonbDebeziumConnectorConfigurationGenerator implements DebeziumConnectorConfigurationGenerator {

    final String mutableHostname;
    final String mutableUsername;
    final String mutablePassword;
    final Integer mutablePort;
    final String mutableDbname;
    final Integer nbOfPartitionsInEventTopic;
    final Boolean slotDropOnStop;

    public JsonbDebeziumConnectorConfigurationGenerator(@ConfigProperty(name = "connector.mutable.database.hostname") final String mutableHostname,
                                                        @ConfigProperty(name = "connector.mutable.database.username") final String mutableUsername,
                                                        @ConfigProperty(name = "connector.mutable.database.password") final String mutablePassword,
                                                        @ConfigProperty(name = "connector.mutable.database.port") final Integer mutablePort,
                                                        @ConfigProperty(name = "connector.mutable.database.dbname") final String mutableDbname,
                                                        @ConfigProperty(name = "connector.mutable.nbOfPartitionsInEventTopic") final Integer nbOfPartitionsInEventTopic,
                                                        @ConfigProperty(name = "slot.drop.on.stop") final Boolean slotDropOnStop) {
        this.mutableHostname = Objects.requireNonNull(mutableHostname);
        this.mutableUsername = Objects.requireNonNull(mutableUsername);
        this.mutablePassword = Objects.requireNonNull(mutablePassword);
        this.mutablePort = Objects.requireNonNull(mutablePort);
        this.mutableDbname = Objects.requireNonNull(mutableDbname);
        this.nbOfPartitionsInEventTopic = Objects.requireNonNull(nbOfPartitionsInEventTopic);
        this.slotDropOnStop = Optional.ofNullable(slotDropOnStop)
                .orElse(Boolean.FALSE);
    }

    @Override
    public DebeziumConnectorConfigurationDTO generateConnectorConfiguration(final String connectorName) {
        return DebeziumConnectorConfigurationDTO
                .newBuilder()
                .withName(connectorName)
                .withConfig(DebeziumConnectorConfigurationConfigDTO
                        .newBuilder()
                        .withDatabaseHostname(mutableHostname)
                        .withDatabasePort(mutablePort.toString())
                        .withDatabaseUser(mutableUsername)
                        .withDatabasePassword(mutablePassword)
                        .withDatabaseDbname(mutableDbname)
                        .withDatabaseServerName(mutableHostname)
                        .withNbOfPartitionsInEventTopic(nbOfPartitionsInEventTopic)
                        .withSlotDropOnStop(slotDropOnStop.toString())
                        .build()
                )
                .build();
    }

}
