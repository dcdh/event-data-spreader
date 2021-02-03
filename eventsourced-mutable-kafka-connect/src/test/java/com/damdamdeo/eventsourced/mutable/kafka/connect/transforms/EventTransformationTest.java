package com.damdamdeo.eventsourced.mutable.kafka.connect.transforms;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;

@ExtendWith(MockitoExtension.class)
public class EventTransformationTest {

    private TestLogger logger = TestLoggerFactory.getTestLogger(EventTransformation.class);

    private EventTransformation eventTransformation;

    @BeforeEach
    public void setup() {
        eventTransformation = new EventTransformation();
        TestLoggerFactory.clear();
    }

    @Test
    public void should_log_when_there_is_only_one_partition_associated_with_the_topic() {
        // Given
        eventTransformation.configure(Collections.singletonMap("nbOfPartitionsInEventTopic", 1));

        // When
        eventTransformation.apply(mock(SourceRecord.class));

        // Then
        assertThat(logger.getLoggingEvents())
                .containsExactly(info("Only one partition defined for topic event, event routed to the first partition"));
    }

    @Test
    public void should_route_event_to_the_first_partition_when_an_event_can_be_route_one_the_first_partition_of_a_topic_having_two_partitions() {
        // Given
        eventTransformation.configure(Collections.singletonMap("nbOfPartitionsInEventTopic", 2));
        final Struct key = keyForTest("aggregateRootType", "b", 0l);
        final SourceRecord givenConnectRecord = mock(SourceRecord.class);
        doReturn(key).when(givenConnectRecord).key();

        // When
        eventTransformation.apply(givenConnectRecord);

        // Then
        verify(givenConnectRecord, times(1)).newRecord(any(), eq(0), any(), any(), any(), any(), any());
    }

    @Test
    public void should_route_event_to_the_second_partition_when_an_event_can_be_route_one_the_first_partition_of_a_topic_having_two_partitions() {
        // Given
        eventTransformation.configure(Collections.singletonMap("nbOfPartitionsInEventTopic", 2));
        final Struct key = keyForTest("aggregateRootType", "a", 0l);
        final SourceRecord givenConnectRecord = mock(SourceRecord.class);
        doReturn(key).when(givenConnectRecord).key();

        // When
        eventTransformation.apply(givenConnectRecord);

        // Then
        verify(givenConnectRecord, times(1)).newRecord(any(), eq(1), any(), any(), any(), any(), any());
    }

    @Test
    public void should_route_event_to_the_second_partition_produces_a_log_when_an_event_can_be_route_one_the_first_partition_of_a_topic_having_two_partitions() {
        // Given
        eventTransformation.configure(Collections.singletonMap("nbOfPartitionsInEventTopic", 2));
        final Struct key = keyForTest("aggregateRootType", "a", 0l);
        final SourceRecord givenConnectRecord = mock(SourceRecord.class);
        doReturn(key).when(givenConnectRecord).key();

        // When
        eventTransformation.apply(givenConnectRecord);

        // Then
        assertThat(logger.getLoggingEvents())
                .containsExactly(info("Route event having aggregate type 'aggregateRootType' with identifier 'a' and version '0' into partition '1' for topic event having '2' partitions defined"));
    }

    @ParameterizedTest
    @CsvSource({"aggregateRootType,b,0,0","aggregateRootType,b,1,0",
            "aggregateRootType,a,0,1","aggregateRootType,a,1,1"})
    public void should_route_event_in_expected_partition(final String givenAggregateRootType,
                                                         final String givenAggregateRootId,
                                                         final Long givenVersion,
                                                         final Integer expectedPartition) {
        // Given
        final Struct key = keyForTest(givenAggregateRootType, givenAggregateRootId, givenVersion);
        final SourceRecord givenConnectRecord = mock(SourceRecord.class);
        doReturn(key).when(givenConnectRecord).key();
        eventTransformation.configure(Collections.singletonMap("nbOfPartitionsInEventTopic", 2));

        // When
        eventTransformation.apply(givenConnectRecord);

        // Then
        verify(givenConnectRecord, times(1)).newRecord(any(), eq(expectedPartition), any(), any(), any(), any(), any());
    }

    private Struct keyForTest(final String aggregateRootType,
                              final String aggregateRootId,
                              final Long givenVersion) {
        final Schema schema = SchemaBuilder.struct()
                .field("aggregateroottype", Schema.STRING_SCHEMA)
                .field("aggregaterootid", Schema.STRING_SCHEMA)
                .field("version", Schema.INT64_SCHEMA)
                .build();
        return new Struct(schema)
                .put("aggregateroottype", aggregateRootType)
                .put("aggregaterootid", aggregateRootId)
                .put("version", givenVersion);
    }

}
