package com.damdamdeo.eventsourced.mutable.infra.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Collections;
import java.util.Map;

public class InfraQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(InfraQuarkusTestResourceLifecycleManager.class);

    private Network network;

    private OkdPostgreSQLContainer<?> postgresMutableContainer;

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
        System.setProperty("quarkus.datasource.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.mutable.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.mutable.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.mutable.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.consumed-events.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.consumed-events.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.consumed-events.password", postgresMutableContainer.getPassword());
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
        if (postgresMutableContainer != null) {
            postgresMutableContainer.close();
        }
        if (network != null) {
            network.close();
        }
    }

}
