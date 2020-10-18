package com.damdamdeo.eventsourced.mutable.infra.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.Map;

public class InfraQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(InfraQuarkusTestResourceLifecycleManager.class);

    private Network network;

    private OkdPostgreSQLContainer<?> postgresMutableContainer;

    private GenericContainer hazelcastContainer;

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
        hazelcastContainer = new GenericContainer("hazelcast/hazelcast:4.0.3")
                .withExposedPorts(5701)
                .waitingFor(
                        Wait.forLogMessage(".*is STARTED.*\\n", 1)
                );
        hazelcastContainer.start();
        hazelcastContainer.followOutput(logConsumer);

        System.setProperty("quarkus.hazelcast-client.cluster-name", "dev");
        System.setProperty("quarkus.hazelcast-client.cluster-members", String.format("localhost:%d", hazelcastContainer.getMappedPort(5701)));
        // System.setProperty("quarkus.hazelcast-client.outbound-port-definitions",);
        // System.setProperty("quarkus.hazelcast-client.outbound-ports", );
        // System.setProperty("quarkus.hazelcast-client.labels",);
        // System.setProperty("quarkus.hazelcast-client.connection-timeout", "60");

        System.setProperty("quarkus.datasource.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.mutable.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.mutable.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.mutable.password", postgresMutableContainer.getPassword());
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
        System.clearProperty("quarkus.hazelcast-client.cluster-name");
        System.clearProperty("quarkus.hazelcast-client.cluster-members");
        System.clearProperty("quarkus.hazelcast-client.outbound-port-definitions");
        System.clearProperty("quarkus.hazelcast-client.outbound-ports");
        System.clearProperty("quarkus.hazelcast-client.labels");
        System.clearProperty("quarkus.hazelcast-client.connection-timeout");

        if (postgresMutableContainer != null) {
            postgresMutableContainer.close();
        }
        if (network != null) {
            network.close();
        }
        if (hazelcastContainer != null) {
            hazelcastContainer.close();
        }
    }

}
