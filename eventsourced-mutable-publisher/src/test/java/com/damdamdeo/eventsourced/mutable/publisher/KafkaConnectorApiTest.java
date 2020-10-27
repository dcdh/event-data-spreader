package com.damdamdeo.eventsourced.mutable.publisher;

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

    @ConfigProperty(name = "kafka-connector-api/mp-rest/url")
    String kafkaConnectorRemoteApi;

    @Inject
    DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator;

    @BeforeEach
    @AfterEach
    public void purge() {
        RestAssured.given()
                .when()
                .delete(kafkaConnectorRemoteApi+ "/connectors/event-sourced-connector");
    }

    @Test
    public void should_register_event_sourced_connector() {
        // Given
        final String connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration();

        // When
        kafkaConnectorApi.registerConnector(connectorConfiguration);
        waitUntilConnectorIsRunning("event-sourced-connector");

        // Then
        RestAssured.given()
                .when()
                .get(kafkaConnectorRemoteApi + "/connectors/event-sourced-connector")
                .then()
                .statusCode(200);
    }

    @Test
    public void should_get_all_connectors_return_event_sourced_connector() {
        // Given
        final String connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration();
        RestAssured.given()
                .accept("application/json")
                .contentType("application/json")
                .body(connectorConfiguration)
                .when()
                .post(kafkaConnectorRemoteApi + "/connectors")
                .then()
                .log().all()
                .statusCode(201);
        waitUntilConnectorIsRunning("event-sourced-connector");

        // When
        final List<String> connectors = kafkaConnectorApi.getAllConnectors();

        // Then
        assertEquals(Arrays.asList("event-sourced-connector"), connectors);
    }

    @Test
    public void should_get_event_sourced_connector_state() {
        // Given
        final String connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration();
        RestAssured.given()
                .accept("application/json")
                .contentType("application/json")
                .body(connectorConfiguration)
                .when()
                .post(kafkaConnectorRemoteApi + "/connectors")
                .then()
                .log().all()
                .statusCode(201);
        waitUntilConnectorIsRunning("event-sourced-connector");

        // When
        final KafkaConnectorStatus kafkaConnectorStatus = kafkaConnectorApi.connectorStatus("event-sourced-connector");

        // Then
        assertEquals("event-sourced-connector", kafkaConnectorStatus.name());
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

}
