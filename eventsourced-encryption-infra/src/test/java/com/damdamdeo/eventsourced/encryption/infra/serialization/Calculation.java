package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigInteger;
import java.util.Objects;

public class Calculation {

    private String id;

    @JsonSerialize(using = JacksonBigIntegerEncryptionSerializer.class)
    @JsonDeserialize(using = JacksonBigIntegerEncryptionDeserializer.class)
    private BigInteger result;

    public Calculation() {}

    public Calculation(final String id, final BigInteger result) {
        this.id = id;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public BigInteger getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calculation that = (Calculation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, result);
    }

    @Override
    public String toString() {
        return "Calculation{" +
                "id='" + id + '\'' +
                ", result=" + result +
                '}';
    }
}
