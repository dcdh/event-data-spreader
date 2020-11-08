package com.damdamdeo.eventsourced.mutable.publisher.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.Map;

public class DebeziumQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(DebeziumQuarkusTestResourceLifecycleManager.class);

    private final static String DEBEZIUM_VERSION = "1.3.0.Final";
    private final static Integer KAFKA_PORT = 9092;
    private final static Integer DEBEZIUM_CONNECT_API_PORT = 8083;

    private Network network;

    private OkdPostgreSQLContainer<?> postgresMutableContainer;

    private GenericContainer<?> zookeeperContainer;

    private GenericContainer<?> kafkaContainer;

    private GenericContainer<?> debeziumConnectContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        network = Network.newNetwork();
        postgresMutableContainer = new OkdPostgreSQLContainer<>()
                .withDatabaseName("mutable")
                .withUsername("postgresuser")
                .withPassword("postgrespassword")
                .withNetwork(network)
                .withNetworkAliases("mutable");
        postgresMutableContainer.start();
        postgresMutableContainer.followOutput(logConsumer);
        System.setProperty("quarkus.datasource.mutable.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.mutable.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.mutable.password", postgresMutableContainer.getPassword());

        zookeeperContainer = new GenericContainer<>("debezium/zookeeper:" + DEBEZIUM_VERSION)
                .withNetwork(network)
                .waitingFor(Wait.forLogMessage(".*Started.*", 1));
        zookeeperContainer.start();
        kafkaContainer = new GenericContainer<>("debezium/kafka:" + DEBEZIUM_VERSION)
                .withNetwork(network)
                .withExposedPorts(KAFKA_PORT)
                .withEnv("ZOOKEEPER_CONNECT", String.format("%s:%d", zookeeperContainer.getNetworkAliases().get(0), 2181))
                .withEnv("CREATE_TOPICS", "event:3:1:compact") // 3 partitions 1 replica
                .waitingFor(Wait.forLogMessage(".*started.*", 1));
        kafkaContainer.start();
        kafkaContainer.followOutput(logConsumer);
        debeziumConnectContainer = new GenericContainer<>("damdamdeo/eventsourced-mutable-kafka-connect:1.3.0.Final")
                .withNetwork(network)
                .withExposedPorts(DEBEZIUM_CONNECT_API_PORT)
                .withEnv("BOOTSTRAP_SERVERS", String.format("%s:%d", kafkaContainer.getNetworkAliases().get(0), KAFKA_PORT))
                .withEnv("GROUP_ID", "1")
                .withEnv("CONFIG_STORAGE_TOPIC", "my_connect_configs")
                .withEnv("OFFSET_STORAGE_TOPIC", "my_connect_offsets")
                .withEnv("STATUS_STORAGE_TOPIC", "my_connect_statuses")
                .waitingFor(Wait.forLogMessage(".*Finished starting connectors and tasks.*", 1));
        debeziumConnectContainer.start();
        debeziumConnectContainer.followOutput(logConsumer);
        System.setProperty("mp.messaging.incoming.event-in.bootstrap.servers",
                String.format("%s:%s", "localhost", kafkaContainer.getMappedPort(KAFKA_PORT)));
        System.setProperty("kafka-connector-api/mp-rest/url",
                String.format("http://%s:%d", "localhost", debeziumConnectContainer.getMappedPort(DEBEZIUM_CONNECT_API_PORT)));
        System.setProperty("connector.mutable.database.hostname", "mutable");
        System.setProperty("connector.mutable.database.username", postgresMutableContainer.getUsername());
        System.setProperty("connector.mutable.database.password", postgresMutableContainer.getPassword());
        System.setProperty("connector.mutable.database.port", "5432");
        System.setProperty("connector.mutable.database.dbname", postgresMutableContainer.getDatabaseName());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.datasource.mutable.jdbc.url");
        System.clearProperty("quarkus.datasource.mutable.username");
        System.clearProperty("quarkus.datasource.mutable.password");
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
        if (zookeeperContainer != null) {
            zookeeperContainer.close();
        }
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
        if (debeziumConnectContainer != null) {
            debeziumConnectContainer.close();
        }
        if (network != null) {
            network.close();
        }
    }

}
