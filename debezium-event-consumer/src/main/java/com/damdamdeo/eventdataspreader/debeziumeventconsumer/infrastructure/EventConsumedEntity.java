package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumed;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumerConsumed;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.KafkaSource;
import com.damdamdeo.eventdataspreader.event.api.EventId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Table(name = "EventConsumed")
@Entity
@NamedQuery(name = "Events.findByEventId",
        query = "SELECT e FROM EventConsumedEntity e  LEFT JOIN FETCH e.eventConsumerEntities " +
                "WHERE e.eventConsumedId.aggregateRootId = :aggregateRootId " +
                "AND e.eventConsumedId.aggregateRootType = :aggregateRootType " +
                "AND e.eventConsumedId.version = :version")
public class EventConsumedEntity implements EventConsumed {

    @EmbeddedId
    private EventConsumedId eventConsumedId;

    @NotNull
    private Boolean consumed;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "aggregateRootId"),
            @JoinColumn(name = "aggregateRootType"),
            @JoinColumn(name = "version")
    })
    private List<EventConsumerConsumedEntity> eventConsumerEntities;

    @NotNull
    private Integer kafkaPartition;

    @NotNull
    @Column(columnDefinition="TEXT")
    private String kafkaTopic;

    @NotNull
    private Long kafkaOffset;

    public EventConsumedEntity() {}

    public EventConsumedEntity(final EventId eventId, final KafkaSource kafkaSource) {
        this.eventConsumedId = new EventConsumedId(eventId);
        this.consumed = Boolean.FALSE;
        this.eventConsumerEntities = new ArrayList<>();
        this.kafkaPartition = Objects.requireNonNull(kafkaSource.partition());
        this.kafkaTopic = Objects.requireNonNull(kafkaSource.topic());
        this.kafkaOffset = Objects.requireNonNull(kafkaSource.offset());
    }

    public void addNewEventConsumerConsumed(final Class consumerClass,
                                            final Date consumedAt,
                                            final String gitCommitId) {
        eventConsumerEntities.add(
                new EventConsumerConsumedEntity(
                        new EventConsumerId(eventConsumedId, consumerClass),
                        consumedAt, gitCommitId));
    }

    public void markAsConsumed() {
        consumed = Boolean.TRUE;
    }

    @Override
    public EventId eventId() {
        return eventConsumedId;
    }

    @Override
    public Boolean consumed() {
        return consumed;
    }

    @Override
    public List<? extends EventConsumerConsumed> eventConsumerConsumeds() {
        return eventConsumerEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventConsumedEntity)) return false;
        EventConsumedEntity that = (EventConsumedEntity) o;
        return Objects.equals(eventConsumedId, that.eventConsumedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventConsumedId);
    }

    @Override
    public String toString() {
        return "EventConsumedEntity{" +
                "eventConsumedId=" + eventConsumedId +
                ", consumed=" + consumed +
                ", eventConsumerEntities=" + eventConsumerEntities +
                ", kafkaPartition=" + kafkaPartition +
                ", kafkaTopic='" + kafkaTopic + '\'' +
                ", kafkaOffset=" + kafkaOffset +
                '}';
    }
}
