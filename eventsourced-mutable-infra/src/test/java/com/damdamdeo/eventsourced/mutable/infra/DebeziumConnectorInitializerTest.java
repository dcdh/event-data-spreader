package com.damdamdeo.eventsourced.mutable.infra;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DebeziumConnectorInitializerTest {

    @ConfigProperty(name = "kafka-connector-api/mp-rest/url")
    String kafkaConnectorRemoteApi;

    @Test
    public void should_event_sourced_connector_be_initialised_at_application_startup() {
        RestAssured.given()
                .when()
                .get(kafkaConnectorRemoteApi + "/connectors/event-sourced-connector")
                .then()
                .statusCode(200);
    }

}
