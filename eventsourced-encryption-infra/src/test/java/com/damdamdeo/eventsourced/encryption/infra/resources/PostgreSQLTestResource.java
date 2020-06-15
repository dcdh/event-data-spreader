package com.damdamdeo.eventsourced.encryption.infra.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.Map;

public class PostgreSQLTestResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer<?> postgresContainer;

    @Override
    public Map<String, String> start() {
        postgresContainer = new PostgreSQLContainer<>("debezium/postgres:11-alpine")
                .withDatabaseName("secret-store")
                .withUsername("postgresql")
                .withPassword("postgresql");
        postgresContainer.start();
        System.setProperty("quarkus.datasource.secret-store.jdbc.url", postgresContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.secret-store.username", postgresContainer.getUsername());
        System.setProperty("quarkus.datasource.secret-store.password", postgresContainer.getPassword());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.datasource.secret-store.jdbc.url");
        System.clearProperty("quarkus.datasource.secret-store.username");
        System.clearProperty("quarkus.datasource.secret-store.password");
        if (postgresContainer != null) {
            postgresContainer.close();
        }
    }

}
