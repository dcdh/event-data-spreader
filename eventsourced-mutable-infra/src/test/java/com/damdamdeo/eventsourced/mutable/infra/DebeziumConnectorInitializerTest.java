package com.damdamdeo.eventsourced.mutable.infra;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DebeziumConnectorInitializerTest {

    @Test
    public void should_event_sourced_connector_be_initialised_at_application_startup() {
        RestAssured.given()
                .when()
                .get("http://localhost:8083/connectors/event-sourced-connector")
                .then()
                .statusCode(200);
    }

}
