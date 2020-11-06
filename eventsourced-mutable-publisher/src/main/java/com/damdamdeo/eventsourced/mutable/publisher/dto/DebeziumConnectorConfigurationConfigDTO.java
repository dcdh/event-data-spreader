package com.damdamdeo.eventsourced.mutable.publisher.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class DebeziumConnectorConfigurationConfigDTO {

    private final String connectorClass;
    private final String tasksMax;
    private final String pluginName;
    private final String databaseHostname;
    private final String databasePort;
    private final String databaseUser;
    private final String databasePassword;
    private final String databaseDbname;
    private final String databaseServerName;
    private final String tableWhitelist;
    private final String snapshotMode;
    private final String transforms;
    private final String transformsRouteType;
    private final String transformsRouteRegex;
    private final String transformsRouteReplacement;
    private final String keyConverter;
    private final String keyConverterSchemasEnable;
    private final String valueConverter;
    private final String valueConverterSchemasEnable;
    private final String partitionerClass;
    private final String includeSchemaChanges;
    private final String tombstonesOnDelete;
    private final String slotDropOnStop;

    private DebeziumConnectorConfigurationConfigDTO(final Builder builder) {
        this.connectorClass = "io.debezium.connector.postgresql.PostgresConnector";
        this.tasksMax = "1";
        this.pluginName = "wal2json";
        this.databaseHostname = Objects.requireNonNull(builder.databaseHostname);
        this.databasePort = Objects.requireNonNull(builder.databasePort);
        this.databaseUser = Objects.requireNonNull(builder.databaseUser);
        this.databasePassword = Objects.requireNonNull(builder.databasePassword);
        this.databaseDbname = Objects.requireNonNull(builder.databaseDbname);
        this.databaseServerName = Objects.requireNonNull(builder.databaseServerName);
        this.tableWhitelist = "public.event";
        this.snapshotMode = "always";
        this.transforms = "route";
        this.transformsRouteType = "org.apache.kafka.connect.transforms.RegexRouter";
        this.transformsRouteRegex = "([^.]+)\\.([^.]+)\\.([^.]+)";
        this.transformsRouteReplacement = "$3";
        this.keyConverter = "org.apache.kafka.connect.json.JsonConverter";
        this.keyConverterSchemasEnable = "false";
        this.valueConverter = "org.apache.kafka.connect.json.JsonConverter";
        this.valueConverterSchemasEnable = "false";
        this.partitionerClass = "com.damdamdeo.eventsourced.mutable.kafka.connect.partitioner.EventPartitioner";
        this.includeSchemaChanges = "false";
        this.tombstonesOnDelete = "false";
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

        public DebeziumConnectorConfigurationConfigDTO build() {
            return new DebeziumConnectorConfigurationConfigDTO(this);
        }
    }

    @JsonbProperty("connector.class")
    public String getConnectorClass() {
        return connectorClass;
    }

    @JsonbProperty("tasks.max")
    public String getTasksMax() {
        return tasksMax;
    }

    @JsonbProperty("plugin.name")
    public String getPluginName() {
        return pluginName;
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

    @JsonbProperty("table.whitelist")
    public String getTableWhitelist() {
        return tableWhitelist;
    }

    @JsonbProperty("snapshot.mode")
    public String getSnapshotMode() {
        return snapshotMode;
    }

    @JsonbProperty("transforms")
    public String getTransforms() {
        return transforms;
    }

    @JsonbProperty("transforms.route.type")
    public String getTransformsRouteType() {
        return transformsRouteType;
    }

    @JsonbProperty("transforms.route.regex")
    public String getTransformsRouteRegex() {
        return transformsRouteRegex;
    }

    @JsonbProperty("transforms.route.replacement")
    public String getTransformsRouteReplacement() {
        return transformsRouteReplacement;
    }

    @JsonbProperty("key.converter")
    public String getKeyConverter() {
        return keyConverter;
    }

    @JsonbProperty("key.converter.schemas.enable")
    public String getKeyConverterSchemasEnable() {
        return keyConverterSchemasEnable;
    }

    @JsonbProperty("value.converter")
    public String getValueConverter() {
        return valueConverter;
    }

    @JsonbProperty("value.converter.schemas.enable")
    public String getValueConverterSchemasEnable() {
        return valueConverterSchemasEnable;
    }

    @JsonbProperty("partitioner.class")
    public String getPartitionerClass() {
        return partitionerClass;
    }

    @JsonbProperty("include.schema.changes")
    public String getIncludeSchemaChanges() {
        return includeSchemaChanges;
    }

    @JsonbProperty("tombstones.on.delete")
    public String getTombstonesOnDelete() {
        return tombstonesOnDelete;
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
        return Objects.equals(connectorClass, that.connectorClass) &&
                Objects.equals(tasksMax, that.tasksMax) &&
                Objects.equals(pluginName, that.pluginName) &&
                Objects.equals(databaseHostname, that.databaseHostname) &&
                Objects.equals(databasePort, that.databasePort) &&
                Objects.equals(databaseUser, that.databaseUser) &&
                Objects.equals(databasePassword, that.databasePassword) &&
                Objects.equals(databaseDbname, that.databaseDbname) &&
                Objects.equals(databaseServerName, that.databaseServerName) &&
                Objects.equals(tableWhitelist, that.tableWhitelist) &&
                Objects.equals(snapshotMode, that.snapshotMode) &&
                Objects.equals(transforms, that.transforms) &&
                Objects.equals(transformsRouteType, that.transformsRouteType) &&
                Objects.equals(transformsRouteRegex, that.transformsRouteRegex) &&
                Objects.equals(transformsRouteReplacement, that.transformsRouteReplacement) &&
                Objects.equals(keyConverter, that.keyConverter) &&
                Objects.equals(keyConverterSchemasEnable, that.keyConverterSchemasEnable) &&
                Objects.equals(valueConverter, that.valueConverter) &&
                Objects.equals(valueConverterSchemasEnable, that.valueConverterSchemasEnable) &&
                Objects.equals(partitionerClass, that.partitionerClass) &&
                Objects.equals(includeSchemaChanges, that.includeSchemaChanges) &&
                Objects.equals(tombstonesOnDelete, that.tombstonesOnDelete) &&
                Objects.equals(slotDropOnStop, that.slotDropOnStop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorClass, tasksMax, pluginName, databaseHostname, databasePort, databaseUser, databasePassword, databaseDbname, databaseServerName, tableWhitelist, snapshotMode, transforms, transformsRouteType, transformsRouteRegex, transformsRouteReplacement, keyConverter, keyConverterSchemasEnable, valueConverter, valueConverterSchemasEnable, partitionerClass, includeSchemaChanges, tombstonesOnDelete, slotDropOnStop);
    }

    @Override
    public String toString() {
        return "DebeziumConnectorConfigurationConfigDTO{" +
                "connectorClass='" + connectorClass + '\'' +
                ", tasksMax='" + tasksMax + '\'' +
                ", pluginName='" + pluginName + '\'' +
                ", databaseHostname='" + "*****" + '\'' +
                ", databasePort='" + "*****" + '\'' +
                ", databaseUser='" + "*****" + '\'' +
                ", databasePassword='" + "*****" + '\'' +
                ", databaseDbname='" + "*****" + '\'' +
                ", databaseServerName='" + "*****" + '\'' +
                ", tableWhitelist='" + tableWhitelist + '\'' +
                ", snapshotMode='" + snapshotMode + '\'' +
                ", transforms='" + transforms + '\'' +
                ", transformsRouteType='" + transformsRouteType + '\'' +
                ", transformsRouteRegex='" + transformsRouteRegex + '\'' +
                ", transformsRouteReplacement='" + transformsRouteReplacement + '\'' +
                ", keyConverter='" + keyConverter + '\'' +
                ", keyConverterSchemasEnable='" + keyConverterSchemasEnable + '\'' +
                ", valueConverter='" + valueConverter + '\'' +
                ", valueConverterSchemasEnable='" + valueConverterSchemasEnable + '\'' +
                ", partitionerClass='" + partitionerClass + '\'' +
                ", includeSchemaChanges='" + includeSchemaChanges + '\'' +
                ", tombstonesOnDelete='" + tombstonesOnDelete + '\'' +
                ", slotDropOnStop='" + slotDropOnStop + '\'' +
                '}';
    }
}
