package com.damdamdeo.eventsourced.mutable.infra.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.Map;

public class PostgreSQLTestResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer<?> postgresMutableContainer;

    @Override
    public Map<String, String> start() {
        // I can't use my image 'dcdh1983/postgresql-10-debezium-centos7:latest' because environment variables and
        // cmd use to run container is hardcoded in PostgreSQLContainer and do not reflect my image
        // I could write one but I also do a big e2e test in OKD for a real application. I will write a specific one in my todo-app ;)
        postgresMutableContainer = new PostgreSQLContainer<>("debezium/postgres:11")
                .withDatabaseName("mutable")
                .withUsername("postgresuser")
                .withPassword("postgrespassword");
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
        System.clearProperty("connector.mutable.database.hostname");
        System.clearProperty("connector.mutable.database.username");
        System.clearProperty("connector.mutable.database.password");
        System.clearProperty("connector.mutable.database.port");
        System.clearProperty("connector.mutable.database.dbname");
        if (postgresMutableContainer != null) {
            postgresMutableContainer.close();
        }
    }

}
