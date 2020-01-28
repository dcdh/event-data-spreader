package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Table(name = "AggregateRoot")
@Entity
public class AggregateRootEntity {

    @EmbeddedId
    private AggregateRootProjectionId aggregateRootProjectionId;

    @NotNull
    @Type(type = "com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.JsonbAsStringUserType")
    @Column(columnDefinition = "jsonb")
    private String aggregateRoot;

    @NotNull
    private Long version;

    public AggregateRootEntity() {}

    public AggregateRootEntity(final AggregateRoot aggregateRoot,
                               final AggregateRootSerializer aggregateRootSerializer,
                               final EncryptedEventSecret encryptedEventSecret) {
        this.aggregateRootProjectionId = new AggregateRootProjectionId(aggregateRoot.aggregateRootId(),
                aggregateRoot.aggregateRootType());
        this.aggregateRoot = aggregateRootSerializer.serialize(encryptedEventSecret, aggregateRoot);
        this.version = aggregateRoot.version();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootEntity that = (AggregateRootEntity) o;
        return Objects.equals(aggregateRootProjectionId, that.aggregateRootProjectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootProjectionId);
    }
}
