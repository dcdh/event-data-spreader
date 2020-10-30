package com.damdamdeo.eventsourced.mutable.kafka.connect.partitioner;

// cf. https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/clients/producer/internals/DefaultPartitioner.java
/**
 * This Partitioner will be used by debezium connect.
 * The debezium connect is setup to use "org.apache.kafka.connect.json.JsonConverter" as key converter. (cf. debezium.json)
 *
 * https://github.com/a0x8o/kafka/blob/master/connect/json/src/main/java/org/apache/kafka/connect/json/JsonConverter.java
 * key should be a map
 */

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventPartitioner implements Partitioner {

    public static final class AggregateRootEventId {

        private final String aggregateRootType;
        private final String aggregateRootId;
        private final Long version;

        public AggregateRootEventId(final Map key) {
            this.aggregateRootType = Objects.requireNonNull((String) key.get("aggregateRootType"));
            this.aggregateRootId = Objects.requireNonNull((String) key.get("aggregateRootId"));
            this.version = Objects.requireNonNull((Long) key.get("version"));
        }

        public Integer targetPartition(final Integer nbOfPartitonsForTopic) {
            // cf. https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/clients/producer/internals/DefaultPartitioner.java
            // hashCode can return negative value. Avoid it by applying a mask to remove the negative bit.
            return Utils.toPositive(new AggregateRootId(aggregateRootType, aggregateRootId).hashCode()) % nbOfPartitonsForTopic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AggregateRootEventId)) return false;
            AggregateRootEventId that = (AggregateRootEventId) o;
            return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                    Objects.equals(aggregateRootId, that.aggregateRootId) &&
                    Objects.equals(version, that.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aggregateRootType, aggregateRootId, version);
        }

        @Override
        public String toString() {
            return "AggregateRootEventId{" +
                    "aggregateRootType='" + aggregateRootType + '\'' +
                    ", aggregateRootId='" + aggregateRootId + '\'' +
                    ", version=" + version +
                    '}';
        }
    }

    public static final class AggregateRootId {

        private final String aggregateRootType;
        private final String aggregateRootId;

        public AggregateRootId(final String aggregateRootType, final String aggregateRootId) {
            this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
            this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AggregateRootId)) return false;
            AggregateRootId that = (AggregateRootId) o;
            return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                    Objects.equals(aggregateRootId, that.aggregateRootId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aggregateRootType, aggregateRootId);
        }

        @Override
        public String toString() {
            return "AggregateRootId{" +
                    "aggregateRootType='" + aggregateRootType + '\'' +
                    ", aggregateRootId='" + aggregateRootId + '\'' +
                    '}';
        }
    }

    private final Logger LOGGER = LoggerFactory.getLogger(EventPartitioner.class);

    @Override
    public int partition(final String topic, final Object key, final byte[] keyBytes, final Object value, final byte[] valueBytes, final Cluster cluster) {
        final Integer nbOfPartitonsForTopic = cluster.partitionCountForTopic(topic);
        if (nbOfPartitonsForTopic.compareTo(1) < 0) {
            throw new IllegalStateException(String.format("Topic '%s' must have at least one partition", topic));
        } else if (nbOfPartitonsForTopic.compareTo(1) == 0) {
            LOGGER.info(String.format("Only one partition defined for topic '%s', event routed to the first partition",
                    topic));
            return 0;
        } else {
            final AggregateRootEventId aggregateRootEventId = new AggregateRootEventId((Map) key);
            final Integer targetPartition = aggregateRootEventId.targetPartition(nbOfPartitonsForTopic);
            if (targetPartition < 0) {
                throw new IllegalStateException("Partition to route on cannot be negative !");
            }
            LOGGER.info(String.format("Route event having aggregate type '%s' with identifier '%s' and version '%d' into partition '%d' for topic '%s' having '%d' partitions",
                    aggregateRootEventId.aggregateRootType, aggregateRootEventId.aggregateRootId, aggregateRootEventId.version, targetPartition, topic, nbOfPartitonsForTopic));
            return targetPartition;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(final Map<String, ?> configs) {}

}
