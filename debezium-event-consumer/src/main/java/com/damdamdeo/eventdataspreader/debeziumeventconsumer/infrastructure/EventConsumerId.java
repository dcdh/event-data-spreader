package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EventConsumerId implements Serializable {

    @Type(type = "pg-uuid")
    private UUID eventId;

    @NotNull
    private String consumerClassName;

    public EventConsumerId() {}

    public EventConsumerId(final UUID eventId,
                           final Class consumerClassName) {
        this.eventId = Objects.requireNonNull(eventId);
        this.consumerClassName = Objects.requireNonNull(consumerClassName.getName());
        Validate.validState(this.consumerClassName.length() < 256);
    }

    public UUID eventId() {
        return eventId;
    }

    public String consumerClassName() {
        return consumerClassName;
    }

}
