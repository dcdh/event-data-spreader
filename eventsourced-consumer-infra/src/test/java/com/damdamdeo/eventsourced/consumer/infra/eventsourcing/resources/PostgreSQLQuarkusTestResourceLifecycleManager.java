package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Collections;
import java.util.Map;

public class PostgreSQLQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(PostgreSQLQuarkusTestResourceLifecycleManager.class);

    private PostgreSQLContainer<?> postgresContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        postgresContainer = new PostgreSQLContainer<>("debezium/postgres:11-alpine")
                .withDatabaseName("consumer")
                .withUsername("postgresql")
                .withPassword("postgresql");
        postgresContainer.start();
        postgresContainer.followOutput(logConsumer);
        System.setProperty("quarkus.datasource.consumed-events.jdbc.url", postgresContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.consumed-events.username", postgresContainer.getUsername());
        System.setProperty("quarkus.datasource.consumed-events.password", postgresContainer.getPassword());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.datasource.consumed-events.jdbc.url");
        System.clearProperty("quarkus.datasource.consumed-events.username");
        System.clearProperty("quarkus.datasource.consumed-events.password");
        if (postgresContainer != null) {
            postgresContainer.close();
        }
    }

}
