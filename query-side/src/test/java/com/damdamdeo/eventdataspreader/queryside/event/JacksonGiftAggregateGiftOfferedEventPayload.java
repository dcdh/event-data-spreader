package com.damdamdeo.eventdataspreader.queryside.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class JacksonGiftAggregateGiftOfferedEventPayload implements GiftAggregateGiftOfferedEventPayload {

    private final String name;
    private final String offeredTo;

    @JsonCreator
    public JacksonGiftAggregateGiftOfferedEventPayload(@JsonProperty("name") final String name,
                                                       @JsonProperty("offeredTo") final String offeredTo) {
        this.name = Objects.requireNonNull(name);
        this.offeredTo = Objects.requireNonNull(offeredTo);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String offeredTo() {
        return offeredTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JacksonGiftAggregateGiftOfferedEventPayload)) return false;
        JacksonGiftAggregateGiftOfferedEventPayload that = (JacksonGiftAggregateGiftOfferedEventPayload) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo);
    }

    @Override
    public String toString() {
        return "GiftOfferedEventPayload{" +
                "name='" + name + '\'' +
                ", offeredTo='" + offeredTo + '\'' +
                '}';
    }

}
