package com.damdamdeo.eventsourced.mutable.publisher;

import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class KafkaConnectorApiTest {

    @Inject
    @RestClient
    KafkaConnectorApi kafkaConnectorApi;

    @ResourcePath("debezium.json")
    Template debeziumTemplate;

    @ConfigProperty(name = "kafka-connector-api/mp-rest/url")
    String kafkaConnectorRemoteApi;

    @ConfigProperty(name = "connector.mutable.database.hostname") String mutableHostname;
    @ConfigProperty(name = "connector.mutable.database.username") String mutableUsername;
    @ConfigProperty(name = "connector.mutable.database.password") String mutablePassword;
    @ConfigProperty(name = "connector.mutable.database.port") Integer mutablePort;
    @ConfigProperty(name = "connector.mutable.database.dbname") String mutableDbname;
    @ConfigProperty(name = "slot.drop.on.stop") Boolean slotDropOnStop;

    @BeforeEach
    @AfterEach
    public void purge() {
        RestAssured.given()
                .when()
                .delete(kafkaConnectorRemoteApi+ "/connectors/test-connector");
    }

    @Test
    public void should_register_test_connector() {
        // Given
        final String connectorConfiguration = connectorConfiguration("test-connector");

        // When
        kafkaConnectorApi.registerConnector(connectorConfiguration);
        waitUntilConnectorIsRunning("test-connector");

        // Then
        RestAssured.given()
                .when()
                .get(kafkaConnectorRemoteApi + "/connectors/test-connector")
                .then()
                .statusCode(200);
    }

    @Test
    public void should_get_all_connectors_return_test_connector() {
        // Given
        final String connectorConfiguration = connectorConfiguration("test-connector");
        RestAssured.given()
                .accept("application/json")
                .contentType("application/json")
                .body(connectorConfiguration)
                .when()
                .post(kafkaConnectorRemoteApi + "/connectors")
                .then()
                .log().all()
                .statusCode(201);
        waitUntilConnectorIsRunning("test-connector");

        // When
        final List<String> connectors = kafkaConnectorApi.getAllConnectors();

        // Then
        assertEquals(Arrays.asList("test-connector", "event-sourced-connector"), connectors);
    }

    @Test
    public void should_get_test_connector_state() {
        // Given
        final String connectorConfiguration = connectorConfiguration("test-connector");
        RestAssured.given()
                .accept("application/json")
                .contentType("application/json")
                .body(connectorConfiguration)
                .when()
                .post(kafkaConnectorRemoteApi + "/connectors")
                .then()
                .log().all()
                .statusCode(201);
        waitUntilConnectorIsRunning("test-connector");

        // When
        final KafkaConnectorStatus kafkaConnectorStatus = kafkaConnectorApi.connectorStatus("test-connector");

        // Then
        assertEquals("test-connector", kafkaConnectorStatus.name());
        assertNotNull(kafkaConnectorStatus.connector());
        assertEquals("RUNNING", kafkaConnectorStatus.connector().state());
        assertNotNull(kafkaConnectorStatus.connector().workerId());
        assertTrue(kafkaConnectorStatus.isRunning());
    }

    private void waitUntilConnectorIsRunning(final String connectorName) {
        Awaitility.await()
                .atMost(Durations.FIVE_SECONDS)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS).until(() ->
                    RestAssured.given()
                            .accept("application/json")
                            .contentType("application/json")
                            .when()
                            .get(String.format("%s/connectors/%s/status", kafkaConnectorRemoteApi, connectorName))
                            .then()
                            .statusCode(200)
                            .extract()
                            .jsonPath().getString("connector.state").equals("RUNNING")
                );
    }

    private String connectorConfiguration(final String connectorName) {
        return this.debeziumTemplate
                .data("name", connectorName)
                .data("databaseHostname", mutableHostname)
                .data("databaseServerName", mutableHostname)
                .data("databaseDbname", mutableDbname)
                .data("databasePort", mutablePort)
                .data("databaseUser", mutableUsername)
                .data("databasePassword", mutablePassword)
                .data("slotDropOnStop", slotDropOnStop)
                .render();
    }
}
