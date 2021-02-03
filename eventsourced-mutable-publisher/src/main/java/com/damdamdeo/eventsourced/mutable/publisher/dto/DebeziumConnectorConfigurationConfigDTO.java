package com.damdamdeo.eventsourced.mutable.publisher.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class DebeziumConnectorConfigurationConfigDTO {

    private final String databaseHostname;
    private final String databasePort;
    private final String databaseUser;
    private final String databasePassword;
    private final String databaseDbname;
    private final String databaseServerName;
    private final Integer transformsEventNbOfPartitionsInEventTopic;
    private final Integer topicCreationEventPartitions;
    private final String slotDropOnStop;

    private DebeziumConnectorConfigurationConfigDTO(final Builder builder) {
        this.databaseHostname = Objects.requireNonNull(builder.databaseHostname);
        this.databasePort = Objects.requireNonNull(builder.databasePort);
        this.databaseUser = Objects.requireNonNull(builder.databaseUser);
        this.databasePassword = Objects.requireNonNull(builder.databasePassword);
        this.databaseDbname = Objects.requireNonNull(builder.databaseDbname);
        this.databaseServerName = Objects.requireNonNull(builder.databaseServerName);
        this.transformsEventNbOfPartitionsInEventTopic = Objects.requireNonNull(builder.nbOfPartitionsInEventTopic);
        this.topicCreationEventPartitions = Objects.requireNonNull(builder.nbOfPartitionsInEventTopic);
        this.slotDropOnStop = Objects.requireNonNull(builder.slotDropOnStop);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String databaseHostname;
        private String databasePort;
        private String databaseUser;
        private String databasePassword;
        private String databaseDbname;
        private String databaseServerName;
        private String slotDropOnStop;
        private Integer nbOfPartitionsInEventTopic;

        public Builder withDatabaseHostname(final String databaseHostname) {
            this.databaseHostname = databaseHostname;
            return this;
        }

        public Builder withDatabasePort(final String databasePort) {
            this.databasePort = databasePort;
            return this;
        }

        public Builder withDatabaseUser(final String databaseUser) {
            this.databaseUser = databaseUser;
            return this;
        }

        public Builder withDatabasePassword(final String databasePassword) {
            this.databasePassword = databasePassword;
            return this;
        }

        public Builder withDatabaseDbname(final String databaseDbname) {
            this.databaseDbname = databaseDbname;
            return this;
        }

        public Builder withDatabaseServerName(final String databaseServerName) {
            this.databaseServerName = databaseServerName;
            return this;
        }

        public Builder withSlotDropOnStop(String slotDropOnStop) {
            this.slotDropOnStop = slotDropOnStop;
            return this;
        }

        public Builder withNbOfPartitionsInEventTopic(Integer nbOfPartitionsInEventTopic) {
            this.nbOfPartitionsInEventTopic = nbOfPartitionsInEventTopic;
            return this;
        }

        public DebeziumConnectorConfigurationConfigDTO build() {
            return new DebeziumConnectorConfigurationConfigDTO(this);
        }
    }

    @JsonbProperty("connector.class")
    public String getConnectorClass() {
        return "io.debezium.connector.postgresql.PostgresConnector";
    }

    @JsonbProperty("tasks.max")
    public String getTasksMax() {
        return "1";
    }

    @JsonbProperty("plugin.name")
    public String getPluginName() {
        return "wal2json";
    }

    @JsonbProperty("database.hostname")
    public String getDatabaseHostname() {
        return databaseHostname;
    }

    @JsonbProperty("database.port")
    public String getDatabasePort() {
        return databasePort;
    }

    @JsonbProperty("database.user")
    public String getDatabaseUser() {
        return databaseUser;
    }

    @JsonbProperty("database.password")
    public String getDatabasePassword() {
        return databasePassword;
    }

    @JsonbProperty("database.dbname")
    public String getDatabaseDbname() {
        return databaseDbname;
    }

    @JsonbProperty("database.server.name")
    public String getDatabaseServerName() {
        return databaseServerName;
    }

    @JsonbProperty("table.include.list")
    public String getTableIncludelist() {
        return "public.event";
    }

    @JsonbProperty("snapshot.mode")
    public String getSnapshotMode() {
        return "always";
    }

    @JsonbProperty("transforms")
    public String getTransforms() {
        return "router,event";
    }

    @JsonbProperty("transforms.router.type")
    public String getTransformsRouterType() {
        return "org.apache.kafka.connect.transforms.RegexRouter";
    }

    @JsonbProperty("transforms.router.regex")
    public String getTransformsRouterRegex() {
        return "([^.]+)\\.([^.]+)\\.([^.]+)";
    }

    @JsonbProperty("transforms.router.replacement")
    public String getTransformsRouterReplacement() {
        return "$3";
    }

    @JsonbProperty("transforms.event.type")
    public String getTransformsEventType() {
        return "com.damdamdeo.eventsourced.mutable.kafka.connect.transforms.EventTransformation";
    }

    @JsonbProperty("transforms.event.nbOfPartitionsInEventTopic")
    public Integer getTransformsEventNbOfPartitionsInEventTopic() {
        return transformsEventNbOfPartitionsInEventTopic;
    }

    @JsonbProperty("topic.creation.default.replication.factor")
    public Integer getTopicCreationDefaultReplicationFactor() {
        return 1;
    }

    @JsonbProperty("topic.creation.default.partitions")
    public Integer getTopicCreationDefaultPartitions() {
        return transformsEventNbOfPartitionsInEventTopic;
    }

    @JsonbProperty("topic.creation.default.cleanup.policy")
    public String getTopicCreationDefaultCleanupPolicy() {
        return "compact";
    }

    @JsonbProperty("key.converter")
    public String getKeyConverter() {
        return "org.apache.kafka.connect.json.JsonConverter";
    }

    @JsonbProperty("key.converter.schemas.enable")
    public String getKeyConverterSchemasEnable() {
        return "false";
    }

    @JsonbProperty("value.converter")
    public String getValueConverter() {
        return "org.apache.kafka.connect.json.JsonConverter";
    }

    @JsonbProperty("value.converter.schemas.enable")
    public String getValueConverterSchemasEnable() {
        return "false";
    }

    @JsonbProperty("partitioner.class")
    public String getPartitionerClass() {
        return "com.damdamdeo.eventsourced.mutable.kafka.connect.partitioner.EventPartitioner";
    }

    @JsonbProperty("include.schema.changes")
    public String getIncludeSchemaChanges() {
        return "false";
    }

    @JsonbProperty("tombstones.on.delete")
    public String getTombstonesOnDelete() {
        return "false";
    }

    @JsonbProperty("slot.drop.on.stop")
    public String getSlotDropOnStop() {
        return slotDropOnStop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumConnectorConfigurationConfigDTO)) return false;
        DebeziumConnectorConfigurationConfigDTO that = (DebeziumConnectorConfigurationConfigDTO) o;
        return Objects.equals(databaseHostname, that.databaseHostname) &&
                Objects.equals(databasePort, that.databasePort) &&
                Objects.equals(databaseUser, that.databaseUser) &&
                Objects.equals(databasePassword, that.databasePassword) &&
                Objects.equals(databaseDbname, that.databaseDbname) &&
                Objects.equals(databaseServerName, that.databaseServerName) &&
                Objects.equals(transformsEventNbOfPartitionsInEventTopic, that.transformsEventNbOfPartitionsInEventTopic) &&
                Objects.equals(topicCreationEventPartitions, that.topicCreationEventPartitions) &&
                Objects.equals(slotDropOnStop, that.slotDropOnStop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseHostname, databasePort, databaseUser, databasePassword, databaseDbname, databaseServerName, transformsEventNbOfPartitionsInEventTopic, topicCreationEventPartitions, slotDropOnStop);
    }

    @Override
    public String toString() {
        return "DebeziumConnectorConfigurationConfigDTO{" +
                ", transformsEventNbOfPartitionsInEventTopic=" + transformsEventNbOfPartitionsInEventTopic +
                ", topicCreationEventPartitions=" + topicCreationEventPartitions +
                ", slotDropOnStop='" + slotDropOnStop + '\'' +
                '}';
    }
}
