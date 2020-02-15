package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

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
    private AggregateRootId aggregateRootId;

    @NotNull
    @Type(type = "com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.JsonbAsStringUserType")
    @Column(columnDefinition = "jsonb")
    private String aggregateRoot;

    @NotNull
    private Long version;

    public AggregateRootEntity() {}

    public AggregateRootEntity(final AggregateRoot aggregateRoot,
                               final AggregateRootSerializer aggregateRootSerializer) {
        this.aggregateRootId = new AggregateRootId(aggregateRoot.aggregateRootId(),
                aggregateRoot.aggregateRootType());
        this.aggregateRoot = aggregateRootSerializer.serialize(aggregateRoot);
        this.version = aggregateRoot.version();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootEntity that = (AggregateRootEntity) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId);
    }
}
