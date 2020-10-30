package com.damdamdeo.eventsourced.mutable.publisher;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class KafkaConnectorStatus {

    private final String name;
    private final Connector connector;

    @JsonbCreator
    public KafkaConnectorStatus(@JsonbProperty("name") final String name,
                                @JsonbProperty("connector") final Connector connector) {
        this.name = name;
        this.connector = connector;
    }

    public String name() {
        return name;
    }

    public Connector connector() {
        return connector;
    }

    public Boolean isRunning() {
        return "RUNNING".equals(this.connector.state());
    }

    public String state() {
        return this.connector.state();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KafkaConnectorStatus that = (KafkaConnectorStatus) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(connector, that.connector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, connector);
    }
}
