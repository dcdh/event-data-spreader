package com.damdamdeo.eventsourced.mutable.kafka.connect.partitioner;

import org.apache.kafka.common.Cluster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;

@ExtendWith(MockitoExtension.class)
public class EventPartitionerTest {

    private EventPartitioner eventPartitioner;

    private TestLogger logger = TestLoggerFactory.getTestLogger(EventPartitioner.class);

    @BeforeEach
    public void setup() {
        eventPartitioner = new EventPartitioner();
        TestLoggerFactory.clear();
    }

    @Test
    public void should_throw_illegal_state_exception_when_no_partition_is_present_in_the_topic() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(0).when(cluster).partitionCountForTopic(anyString());

        // When && Then
        assertThatThrownBy(() -> eventPartitioner.partition("topic", null, null, null, null, cluster))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Topic 'topic' must have at least one partition");
    }

    @Test
    public void should_get_partition_count_for_topic_when_determining_the_partition_to_use() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(1).when(cluster).partitionCountForTopic("topic");

        // When
        eventPartitioner.partition("topic", null, null, null, null, cluster);

        // Then
        verify(cluster, times(1)).partitionCountForTopic(anyString());
    }

    @Test
    public void should_return_partition_one_when_there_is_only_one_partition_associated_with_the_topic() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(1).when(cluster).partitionCountForTopic(anyString());

        // When
        final int partition = eventPartitioner.partition("topic", null, null, null, null, cluster);

        // Then
        assertThat(partition).isEqualTo(0);
    }

    @Test
    public void should_log_when_there_is_only_one_partition_associated_with_the_topic() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(1).when(cluster).partitionCountForTopic(anyString());

        // When
        eventPartitioner.partition("topic", null, null, null, null, cluster);

        // Then
        assertThat(logger.getLoggingEvents())
                .containsExactly(info("Only one partition defined for topic 'topic', event routed to the first partition"));
    }

    @Test
    public void should_route_event_to_the_first_partition_when_an_event_can_be_route_one_the_first_partition_of_a_topic_having_two_partitions() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(2).when(cluster).partitionCountForTopic(anyString());
        final Map<Object, Object> key = keyForTest("aggregateRootType", "b", 0l);

        // When
        final int partition = eventPartitioner.partition("topic", key, null, null, null, cluster);

        // Then
        assertThat(partition).isEqualTo(0);
    }

    @Test
    public void should_route_event_to_the_first_partition_produces_a_log_when_an_event_can_be_route_one_the_first_partition_of_a_topic_having_two_partitions() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(2).when(cluster).partitionCountForTopic(anyString());
        final Map<Object, Object> key = keyForTest("aggregateRootType", "b", 0l);

        // When
        eventPartitioner.partition("topic", key, null, null, null, cluster);

        // Then
        assertThat(logger.getLoggingEvents())
                .containsExactly(info("Route event having aggregate type 'aggregateRootType' with identifier 'b' and version '0' into partition '0' for topic 'topic' having '2' partitions"));
    }

    @Test
    public void should_route_event_to_the_second_partition_when_an_event_can_be_route_one_the_first_partition_of_a_topic_having_two_partitions() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(2).when(cluster).partitionCountForTopic(anyString());
        final Map<Object, Object> key = keyForTest("aggregateRootType", "a", 0l);

        // When
        final int partition = eventPartitioner.partition("topic", key, null, null, null, cluster);

        // Then
        assertThat(partition).isEqualTo(1);
    }

    @Test
    public void should_route_event_to_the_second_partition_produces_a_log_when_an_event_can_be_route_one_the_first_partition_of_a_topic_having_two_partitions() {
        // Given
        final Cluster cluster = mock(Cluster.class);
        doReturn(2).when(cluster).partitionCountForTopic(anyString());
        final Map<Object, Object> key = keyForTest("aggregateRootType", "a", 0l);

        // When
        eventPartitioner.partition("topic", key, null, null, null, cluster);

        // Then
        assertThat(logger.getLoggingEvents())
                .containsExactly(info("Route event having aggregate type 'aggregateRootType' with identifier 'a' and version '0' into partition '1' for topic 'topic' having '2' partitions"));
    }

    @ParameterizedTest
    @CsvSource({"aggregateRootType,b,0,0","aggregateRootType,b,1,0",
                "aggregateRootType,a,0,1","aggregateRootType,a,1,1"})
    public void should_route_event_in_expected_partition(final String givenAggregateRootType,
                                                         final String givenAggregateRootId,
                                                         final Long givenVersion,
                                                         final Integer expectedPartition) {
        // Given
        final Map<Object, Object> key = keyForTest(givenAggregateRootType, givenAggregateRootId, givenVersion);
        final Cluster cluster = mock(Cluster.class);
        doReturn(2).when(cluster).partitionCountForTopic(anyString());

        // When
        final int partition = eventPartitioner.partition("topic", key, null, null, null, cluster);

        // Then
        assertThat(partition).isEqualTo(expectedPartition);
    }

    private Map<Object, Object> keyForTest(final String aggregateRootType,
                                           final String aggregateRootId,
                                           final Long givenVersion) {
        final Map<Object, Object> key = new HashMap<>();
        key.put("aggregateRootType", aggregateRootType);
        key.put("aggregateRootId", aggregateRootId);
        key.put("version", givenVersion);
        return key;
    }

}
