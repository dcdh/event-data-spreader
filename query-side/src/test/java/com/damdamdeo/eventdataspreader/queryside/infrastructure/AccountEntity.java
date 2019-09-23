package com.damdamdeo.eventdataspreader.queryside.infrastructure;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Table(name = "Account")
@Entity
public class AccountEntity {

    @Id
    private String owner;

    @NotNull
    private BigDecimal balance;

    @NotNull
    private Long version;

    public AccountEntity() {}

    public void onAccountDebited(final String owner, final BigDecimal balance, final Long version) {
        this.owner = Objects.requireNonNull(owner);
        this.balance = Objects.requireNonNull(balance);
        this.version = Objects.requireNonNull(version);
    }

    public String owner() {
        return owner;
    }

    public BigDecimal balance() {
        return balance;
    }

    public Long version() {
        return version;
    }

}
