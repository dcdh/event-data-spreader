package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Table(name = "Gift")
@Entity
public class GiftEntity {

    @Id
    private String name;

    private String offeredTo;

    @NotNull
    private Long version;

    public GiftEntity() {}

    public void onGiftBought(final String name, final Long version) {
        this.name = Objects.requireNonNull(name);
        this.version = Objects.requireNonNull(version);
    }

    public void onGiftOffered(final String offeredTo, final Long version) {
        this.offeredTo = Objects.requireNonNull(offeredTo);
        this.version = Objects.requireNonNull(version);
    }

    public String name() {
        return name;
    }

    public String offeredTo() {
        return offeredTo;
    }

    public Long version() {
        return version;
    }

}
