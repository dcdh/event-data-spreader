package com.damdamdeo.eventsourced.mutable.publisher.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class DebeziumConnectorConfigurationDTO {

    private final String name;

    private final DebeziumConnectorConfigurationConfigDTO config;

    private DebeziumConnectorConfigurationDTO(final Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.config = Objects.requireNonNull(builder.config);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private DebeziumConnectorConfigurationConfigDTO config;

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withConfig(final DebeziumConnectorConfigurationConfigDTO config) {
            this.config = config;
            return this;
        }

        public DebeziumConnectorConfigurationDTO build() {
            return new DebeziumConnectorConfigurationDTO(this);
        }

    }

    @JsonbProperty("name")
    public String getName() {
        return name;
    }

    @JsonbProperty("config")
    public DebeziumConnectorConfigurationConfigDTO getConfig() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumConnectorConfigurationDTO)) return false;
        DebeziumConnectorConfigurationDTO that = (DebeziumConnectorConfigurationDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, config);
    }

    @Override
    public String toString() {
        return "DebeziumConnectorConfigurationDTO{" +
                "name='" + name + '\'' +
                ", config=" + config +
                '}';
    }

}
