package com.damdamdeo.eventsourced.mutable.infra.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import io.debezium.testing.testcontainers.DebeziumContainer;

import java.util.Collections;
import java.util.Map;

public class DebeziumQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private Network network;

    private OkdPostgreSQLContainer<?> postgresMutableContainer;

    private KafkaContainer kafkaContainer;

    private DebeziumContainer debeziumContainer;

    @Override
    public Map<String, String> start() {
        network = Network.newNetwork();
        final String networkAliases = "mutable";
        postgresMutableContainer = new OkdPostgreSQLContainer<>()
                .withDatabaseName("mutable")
                .withUsername("postgresuser")
                .withPassword("postgrespassword")
                .withNetwork(network)
                .withNetworkAliases(networkAliases);
        postgresMutableContainer.start();
        System.setProperty("quarkus.datasource.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.mutable.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.mutable.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.mutable.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.consumed-events.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.consumed-events.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.consumed-events.password", postgresMutableContainer.getPassword());
        // confluentinc/cp-kafka:5.2.1
        kafkaContainer = new KafkaContainer("5.2.1")
                .withNetwork(network);
        kafkaContainer.start();
        debeziumContainer = new DebeziumContainer("debezium/connect:1.2.0.Beta2")
                .withNetwork(network)
                .withKafka(kafkaContainer)
                .dependsOn(kafkaContainer);
        debeziumContainer.start();
        System.setProperty("mp.messaging.incoming.event-in.bootstrap.servers", kafkaContainer.getBootstrapServers());
        System.setProperty("kafka-connector-api/mp-rest/url",
                String.format("http://%s:%d", "localhost", debeziumContainer.getMappedPort(8083)));
        System.setProperty("connector.mutable.database.hostname", "mutable");
        System.setProperty("connector.mutable.database.username", postgresMutableContainer.getUsername());
        System.setProperty("connector.mutable.database.password", postgresMutableContainer.getPassword());
        System.setProperty("connector.mutable.database.port", "5432");
        System.setProperty("connector.mutable.database.dbname", postgresMutableContainer.getDatabaseName());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.datasource.jdbc.url");
        System.clearProperty("quarkus.datasource.username");
        System.clearProperty("quarkus.datasource.password");
        System.clearProperty("quarkus.datasource.mutable.jdbc.url");
        System.clearProperty("quarkus.datasource.mutable.username");
        System.clearProperty("quarkus.datasource.mutable.password");
        System.clearProperty("quarkus.datasource.consumed-events.jdbc.url");
        System.clearProperty("quarkus.datasource.consumed-events.username");
        System.clearProperty("quarkus.datasource.consumed-events.password");
        System.clearProperty("kafka-connector-api/mp-rest/url");
        System.clearProperty("connector.mutable.database.hostname");
        System.clearProperty("connector.mutable.database.username");
        System.clearProperty("connector.mutable.database.password");
        System.clearProperty("connector.mutable.database.port");
        System.clearProperty("connector.mutable.database.dbname");
        if (postgresMutableContainer != null) {
            postgresMutableContainer.close();
        }
        System.clearProperty("mp.messaging.incoming.event-in.bootstrap.servers");
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
        if (debeziumContainer != null) {
            debeziumContainer.close();
        }
        if (network != null) {
            network.close();
        }
    }

}
