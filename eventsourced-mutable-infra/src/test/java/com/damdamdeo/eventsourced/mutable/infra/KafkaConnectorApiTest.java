package com.damdamdeo.eventsourced.mutable.infra;

import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class KafkaConnectorApiTest {

    @Inject
    @RestClient
    KafkaConnectorApi kafkaConnectorApi;

    @ResourcePath("debezium.json")
    Template debeziumTemplate;

    @ConfigProperty(name = "kafka-connector-api/mp-rest/url")
    String kafkaConnectorRemoteApi;

    @BeforeEach
    public void purge() {
        RestAssured.given()
                .when()
                .delete(kafkaConnectorRemoteApi + "/connectors/event-sourced-connector");
    }

    @ConfigProperty(name = "connector.mutable.database.hostname") String mutableHostname;
    @ConfigProperty(name = "connector.mutable.database.username") String mutableUsername;
    @ConfigProperty(name = "connector.mutable.database.password") String mutablePassword;
    @ConfigProperty(name = "connector.mutable.database.port") Integer mutablePort;
    @ConfigProperty(name = "connector.mutable.database.dbname") String mutableDbname;
    @ConfigProperty(name = "slot.drop.on.stop") Boolean slotDropOnStop;

    @AfterEach
    public void tearDown() {
        final String connectorConfiguration = connectorConfiguration();
        given()
                .contentType("application/json")
                .accept("application/json")
                .body(connectorConfiguration)
                .when()
                .post(kafkaConnectorRemoteApi + "/connectors/");
    }

    @Test
    public void should_get_all_connectors_return_event_sourced_connector() {
        // Given
        final String connectorConfiguration = connectorConfiguration();

        given()
                .contentType("application/json")
                .accept("application/json")
                .body(connectorConfiguration)
                .when()
                .post(kafkaConnectorRemoteApi + "/connectors/")
                .then()
                .log()
                .all()
                .statusCode(201);

        // When
        final List<String> connectors = kafkaConnectorApi.getAllConnectors();

        // Then
        assertEquals(Collections.singletonList("event-sourced-connector"), connectors);
    }

    @Test
    public void should_register_connector() {
        // Given
        final String connectorConfiguration = connectorConfiguration();

        // When
        kafkaConnectorApi.registerConnector(connectorConfiguration);

        // Then
        RestAssured.given()
                .when()
                .get(kafkaConnectorRemoteApi + "/connectors/event-sourced-connector")
                .then()
                .statusCode(200);
    }

    private String connectorConfiguration() {
        return this.debeziumTemplate.data("databaseHostname", mutableHostname)
                .data("databaseServerName", mutableHostname)
                .data("databaseDbname", mutableDbname)
                .data("databasePort", mutablePort)
                .data("databaseUser", mutableUsername)
                .data("databasePassword", mutablePassword)
                .data("slotDropOnStop", slotDropOnStop)
                .render();
    }
}
