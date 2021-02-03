package com.damdamdeo.eventsourced.mutable.kafka.connect.transforms;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

public final class EventTransformation implements Transformation<SourceRecord> {

    private final Logger LOGGER = LoggerFactory.getLogger(EventTransformation.class);

    private static final String MAX_PARTITIONS_CONFIG = "nbOfPartitionsInEventTopic";
    private static final String MAX_PARTITIONS_DOC = "Number of partitions in event topic";
    private static final int MAX_PARTITIONS_DEFAULT = 1;

    private static final ConfigDef CONFIG_DEF = new ConfigDef().define(MAX_PARTITIONS_CONFIG, Type.INT, MAX_PARTITIONS_DEFAULT, Importance.HIGH, MAX_PARTITIONS_DOC);

    private int nbOfPartitionsInEventTopic;

    @Override
    public SourceRecord apply(final SourceRecord record) {
        final int targetPartition;
        if (nbOfPartitionsInEventTopic == 1) {
            targetPartition = 0;
            LOGGER.info("Only one partition defined for topic event, event routed to the first partition");
        } else {
            final AggregateRootEventId aggregateRootEventId = new AggregateRootEventId(requireStruct(record.key(), "EventTransformation"));
            targetPartition = aggregateRootEventId.targetPartition(nbOfPartitionsInEventTopic);
            LOGGER.info(String.format("Route event having aggregate type '%s' with identifier '%s' and version '%d' into partition '%d' for topic event having '%d' partitions defined",
                    aggregateRootEventId.aggregateRootType(), aggregateRootEventId.aggregateRootId(), aggregateRootEventId.version(), targetPartition, nbOfPartitionsInEventTopic));
        }
        if (targetPartition < 0) {
            throw new IllegalStateException("Partition to route on cannot be negative !");
        }
        return record.newRecord(record.topic(),
                targetPartition,
                record.keySchema(),
                record.key(),
                record.valueSchema(),
                record.value(),
                record.timestamp());
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() {}

    @Override
    public void configure(final Map<String, ?> configs) {
        final AbstractConfig config = new AbstractConfig(CONFIG_DEF, configs);
        nbOfPartitionsInEventTopic = config.getInt(MAX_PARTITIONS_CONFIG);
    }
}
