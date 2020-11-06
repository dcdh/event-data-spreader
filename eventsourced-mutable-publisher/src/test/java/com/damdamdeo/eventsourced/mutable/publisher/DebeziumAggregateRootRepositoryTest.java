package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.encryption.api.AESEncryptionQualifier;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.*;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadsDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootMaterializedStatesDeSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.AggregateRootInstanceCreator;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.DefaultAggregateRootRepository;
import com.jayway.jsonpath.JsonPath;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@QuarkusTest
public class DebeziumAggregateRootRepositoryTest {

    @InjectMock
    AggregateRootInstanceCreator aggregateRootInstanceCreator;

    @Inject
    DefaultAggregateRootRepository defaultAggregateRootRepository;

    @ConfigProperty(name = "mp.messaging.incoming.event-in.bootstrap.servers")
    String bootstrapServers;

    @InjectMock
    AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer;

    @InjectMock
    AggregateRootEventMetadataSerializer aggregateRootEventMetadataSerializer;

    @InjectMock
    AggregateRootMaterializedStatesDeSerializer aggregateRootMaterializedStatesDeSerializer;

    @Inject
    DebeziumConnectorConfigurationGenerator debeziumConnectorConfigurationGenerator;

    @InjectMock
    SecretStore secretStore;

    @InjectMock
    @AESEncryptionQualifier
    Encryption encryption;

    @InjectMock
    GitCommitProvider gitCommitProvider;

    @ConfigProperty(name = "kafka-connector-api/mp-rest/url")
    String kafkaConnectorRemoteApi;

    final RestAssuredConfig restAssuredConfig = RestAssured.config()
            .objectMapperConfig(ObjectMapperConfig.objectMapperConfig().defaultObjectMapperType(ObjectMapperType.JSONB));

    @BeforeEach
    public void setupInjectedServicesMocks() {
        doReturn("secret").when(encryption).generateNewSecret();
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();
        doReturn("{\"payload\": {}}").when(aggregateRootEventPayloadsDeSerializer).serialize(any(), any(), any());
        doReturn("{\"meta\": {}}").when(aggregateRootEventMetadataSerializer).serialize();
        doReturn("{\"materializedState\": {}}").when(aggregateRootMaterializedStatesDeSerializer).serialize(any(), anyBoolean());
    }

    @BeforeEach
    public void waitDebeziumConnectorIsReady() {
        Awaitility.await()
                .atMost(Durations.FIVE_SECONDS)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS).until(() ->
                RestAssured.given()
                        .accept("application/json")
                        .contentType("application/json")
                        .when()
                        .get(String.format("%s/connectors/%s/status", kafkaConnectorRemoteApi, "event-sourced-connector"))
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath().getString("connector.state").equals("RUNNING")
        );
    }

    // https://github.com/debezium/debezium-examples/blob/master/testcontainers/src/test/java/io/debezium/examples/testcontainers/DebeziumContainerTest.java
    @Test
    public void should_store_and_publish_event_in_kafka() {
        // Given
        final Secret mockSecret = mock(Secret.class);
        doReturn(mockSecret).when(secretStore).store(any(), any());

        final AggregateRoot loadedAggregateRootForMaterializedState = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(loadedAggregateRootForMaterializedState.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(loadedAggregateRootForMaterializedState.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(loadedAggregateRootForMaterializedState.version()).thenReturn(0l);
        doReturn(loadedAggregateRootForMaterializedState).when(aggregateRootInstanceCreator).createNewInstance(any(), any());

        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final List<AggregateRootEvent> aggregateRootEvents = singletonList(mockAggregateRootEvent);
        when(mockAggregateRootEvent.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRootEvent.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRootEvent.version()).thenReturn(0l);
        when(mockAggregateRootEvent.eventId().aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRootEvent.eventId().aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRootEvent.eventId().version()).thenReturn(0l);
        doReturn("eventType").when(mockAggregateRootEvent).eventType();
        doReturn(LocalDateTime.now()).when(mockAggregateRootEvent).creationDate();
        doReturn(mock(AggregateRootEventPayload.class)).when(mockAggregateRootEvent).eventPayload();

        doReturn(aggregateRootEvents).when(mockAggregateRoot).unsavedEvents();
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.version()).thenReturn(0l);

        // When
        defaultAggregateRootRepository.save(mockAggregateRoot);

        // Then
        final KafkaConsumer<String, String> consumer = getConsumer(bootstrapServers);

        consumer.subscribe(asList("event"));
        final List<ConsumerRecord<String, String>> changeEvents = drain(consumer, 1);
        final ConsumerRecord<String, String> changeEvent = changeEvents.get(0);

        assertEquals("aggregateRootId", JsonPath.<String> read(changeEvent.key(), "$.aggregaterootid"));
        assertEquals("aggregateRootType", JsonPath.<String> read(changeEvent.key(), "$.aggregateroottype"));
        assertEquals(0, JsonPath.<Integer> read(changeEvent.key(), "$.version"));

        assertNull(JsonPath.<String> read(changeEvent.value(), "$.before"));
        assertEquals("c", JsonPath.<String> read(changeEvent.value(), "$.op"), changeEvent.toString());
        assertEquals("aggregateRootId", JsonPath.<String> read(changeEvent.value(), "$.after.aggregaterootid"));
        assertEquals("aggregateRootType", JsonPath.<String> read(changeEvent.value(), "$.after.aggregateroottype"));
        assertEquals(0, JsonPath.<Integer> read(changeEvent.value(), "$.after.version"));
        assertNotNull(JsonPath.<Long> read(changeEvent.value(), "$.after.creationdate"));

        assertEquals("eventType", JsonPath.<String> read(changeEvent.value(), "$.after.eventtype"));
        assertEquals("{\"meta\": {}}", JsonPath.<String> read(changeEvent.value(), "$.after.eventmetadata"));
        assertEquals("{\"payload\": {}}", JsonPath.<String> read(changeEvent.value(), "$.after.eventpayload"));
        assertEquals("{\"materializedState\": {}}", JsonPath.<String> read(changeEvent.value(), "$.after.materializedstate"));
        assertEquals("3bc9898721c64c5d6d17724bf6ec1c715cca0f69", JsonPath.<String> read(changeEvent.value(), "$.after.gitcommitid"));
    }

    private KafkaConsumer<String, String> getConsumer(final String bootstrapServers) {
        return new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                new StringDeserializer(),
                new StringDeserializer());
    }

    private List<ConsumerRecord<String, String>> drain(final KafkaConsumer<String, String> consumer,
                                                       final int expectedRecordCount) {
        final List<ConsumerRecord<String, String>> allRecords = new ArrayList<>();
        Awaitility.await()
                .atMost(Durations.TEN_SECONDS)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS).until(() -> {
            consumer.poll(java.time.Duration.ofMillis(50))
                    .iterator()
                    .forEachRemaining(allRecords::add);
            return allRecords.size() == expectedRecordCount;
        });
        return allRecords;
    }

}
