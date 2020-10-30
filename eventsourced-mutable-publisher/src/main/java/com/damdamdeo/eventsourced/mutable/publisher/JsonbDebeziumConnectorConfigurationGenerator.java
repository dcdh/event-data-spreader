package com.damdamdeo.eventsourced.mutable.publisher;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
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
    public String generateConnectorConfiguration() {
        final JsonObject configJsonObject = Json.createObjectBuilder()
                .add("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .add("tasks.max", "1")
                .add("plugin.name", "wal2json")
                .add("database.hostname", Json.createValue(mutableHostname))
                .add("database.port", Json.createValue(mutablePort.toString()))
                .add("database.user", Json.createValue(mutableUsername))
                .add("database.password", Json.createValue(mutablePassword))
                .add("database.dbname", Json.createValue(mutableDbname))
                .add("database.server.name", Json.createValue(mutableHostname))
                .add("table.whitelist", "public.event")
                .add("snapshot.mode", "always")
                .add("transforms", "route")
                .add("transforms.route.type", "org.apache.kafka.connect.transforms.RegexRouter")
                .add("transforms.route.regex", "([^.]+)\\.([^.]+)\\.([^.]+)")
                .add("transforms.route.replacement", "$3")
                .add("key.converter", "org.apache.kafka.connect.json.JsonConverter")
                .add("key.converter.schemas.enable", "false")
                .add("value.converter", "org.apache.kafka.connect.json.JsonConverter")
                .add("value.converter.schemas.enable", "false")
                .add("partitioner.class", "com.damdamdeo.eventsourced.mutable.kafka.connect.partitioner.EventPartitioner")
                .add("include.schema.changes", "false")
                .add("tombstones.on.delete", "false")
                .add("slot.drop.on.stop", Json.createValue(slotDropOnStop.toString()))
                .build();
        return Json.createObjectBuilder()
                .add("name", DebeziumConnectorConfigurationGenerator.EVENTSOURCED_CONNECTOR)
                .add("config", configJsonObject)
                .build().toString();
    }

}
