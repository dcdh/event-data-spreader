package com.damdamdeo.eventsourced.mutable.publisher;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class Connector {

    private final String state;
    private final String workerId;

    @JsonbCreator
    public Connector(@JsonbProperty("state") final String state,
                     @JsonbProperty("worker_id") final String workerId) {
        this.state = state;
        this.workerId = workerId;
    }

    public String state() {
        return state;
    }

    public String workerId() {
        return workerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connector connector = (Connector) o;
        return Objects.equals(state, connector.state) &&
                Objects.equals(workerId, connector.workerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, workerId);
    }
}
