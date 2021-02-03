package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.mutable.publisher.dto.DebeziumConnectorConfigurationDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    final RestAssuredConfig restAssuredConfig = RestAssured.config()
            .objectMapperConfig(ObjectMapperConfig.objectMapperConfig().defaultObjectMapperType(ObjectMapperType.JSONB));

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
        final DebeziumConnectorConfigurationDTO connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration("test-connector");

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
        final DebeziumConnectorConfigurationDTO connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration("test-connector");
        RestAssured.given()
                .config(restAssuredConfig)
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
        assertThat(connectors).contains("test-connector");
    }

    @Test
    public void should_get_test_connector_state() {
        // Given
        final DebeziumConnectorConfigurationDTO connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration("test-connector");
        RestAssured.given()
                .config(restAssuredConfig)
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
                            .log().all()
                            .statusCode(200)
                            .extract()
                            .jsonPath().getString("connector.state").equals("RUNNING")
                );
    }

}
