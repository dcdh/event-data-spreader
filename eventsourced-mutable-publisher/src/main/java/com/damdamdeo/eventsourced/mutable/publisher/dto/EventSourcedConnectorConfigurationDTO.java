package com.damdamdeo.eventsourced.mutable.publisher.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class EventSourcedConnectorConfigurationDTO {

    private final String name;

    private final EventSourcedConnectorConfigurationConfigDTO config;

    private EventSourcedConnectorConfigurationDTO(final Builder builder) {
        this.name = "event-sourced-connector";
        this.config = Objects.requireNonNull(builder.config);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private EventSourcedConnectorConfigurationConfigDTO config;

        public Builder withConfig(final EventSourcedConnectorConfigurationConfigDTO config) {
            this.config = config;
            return this;
        }

        public EventSourcedConnectorConfigurationDTO build() {
            return new EventSourcedConnectorConfigurationDTO(this);
        }

    }

    @JsonbProperty("name")
    public String getName() {
        return name;
    }

    @JsonbProperty("config")
    public EventSourcedConnectorConfigurationConfigDTO getConfig() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSourcedConnectorConfigurationDTO)) return false;
        EventSourcedConnectorConfigurationDTO that = (EventSourcedConnectorConfigurationDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, config);
    }

    @Override
    public String toString() {
        return "EventSourcedConnectorConfigurationDTO{" +
                "name='" + name + '\'' +
                ", config=" + config +
                '}';
    }

}
