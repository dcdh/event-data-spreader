package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.mutable.publisher.dto.EventSourcedConnectorConfigurationDTO;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@QuarkusTest
public class DebeziumConnectorConfigurationGeneratorTest {

    @Inject
    DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator;

    @Test
    public void should_generate_expected_connector_configuration() throws Exception {
        // Given

        // When
        final EventSourcedConnectorConfigurationDTO connectorConfiguration = debeziumConnectorConfigurationGenerator.generateConnectorConfiguration();
        final JsonObject jsonConnectorConfiguration = Json.createReader(new StringReader(JsonbBuilder.create().toJson(connectorConfiguration))).readObject();

        // Then
        final JsonObject expectedJsonConnectorConfiguration = Json.createReader(
                new StringReader(
                        new String(Files.readAllBytes(Paths.get(getClass().getResource("/expected/debezium.json").toURI())))))
                .readObject();

        assertEquals(jsonConnectorConfiguration, expectedJsonConnectorConfiguration);
    }

}
