package com.damdamdeo.eventsourced.mutable.publisher;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class DebeziumEventSourcedConnectorInitializerTest {

    @ConfigProperty(name = "kafka-connector-api/mp-rest/url")
    String kafkaConnectorRemoteApi;

    @Test
    public void should_event_sourced_connector_be_initialised_and_running_at_application_startup() {
        RestAssured.given()
                .when()
                .get(kafkaConnectorRemoteApi + "/connectors/event-sourced-connector/status")
                .then()
                .statusCode(200)
                .body("name", equalTo("event-sourced-connector"))
                .body("connector.state", equalTo("RUNNING"))
                .body("connector.worker_id", notNullValue());
    }

    // TODO tester la publication et voir si les events sont bien routées ...
    // Le faire une fois que https://issues.redhat.com/browse/DBZ-2731 fixé

}
