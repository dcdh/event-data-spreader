package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.mutable.publisher.dto.EventSourcedConnectorConfigurationConfigDTO;
import com.damdamdeo.eventsourced.mutable.publisher.dto.EventSourcedConnectorConfigurationDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class JsonbDebeziumConnectorConfigurationGenerator implements DebeziumConnectorConfigurationGenerator {

    final String mutableHostname;
    final String mutableUsername;
    final String mutablePassword;
    final Integer mutablePort;
    final String mutableDbname;
    final Boolean slotDropOnStop;

    public JsonbDebeziumConnectorConfigurationGenerator(@ConfigProperty(name = "connector.mutable.database.hostname") final String mutableHostname,
                                                        @ConfigProperty(name = "connector.mutable.database.username") final String mutableUsername,
                                                        @ConfigProperty(name = "connector.mutable.database.password") final String mutablePassword,
                                                        @ConfigProperty(name = "connector.mutable.database.port") final Integer mutablePort,
                                                        @ConfigProperty(name = "connector.mutable.database.dbname") final String mutableDbname,
                                                        @ConfigProperty(name = "slot.drop.on.stop") final Boolean slotDropOnStop) {
        this.mutableHostname = Objects.requireNonNull(mutableHostname);
        this.mutableUsername = Objects.requireNonNull(mutableUsername);
        this.mutablePassword = Objects.requireNonNull(mutablePassword);
        this.mutablePort = Objects.requireNonNull(mutablePort);
        this.mutableDbname = Objects.requireNonNull(mutableDbname);
        this.slotDropOnStop = slotDropOnStop == null ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public EventSourcedConnectorConfigurationDTO generateConnectorConfiguration() {
        return EventSourcedConnectorConfigurationDTO
                .newBuilder()
                .withConfig(EventSourcedConnectorConfigurationConfigDTO
                        .newBuilder()
                        .withDatabaseHostname(mutableHostname)
                        .withDatabasePort(mutablePort.toString())
                        .withDatabaseUser(mutableUsername)
                        .withDatabasePassword(mutablePassword)
                        .withDatabaseDbname(mutableDbname)
                        .withDatabaseServerName(mutableHostname)
                        .withSlotDropOnStop(slotDropOnStop.toString())
                        .build()
                )
                .build();
    }

}
