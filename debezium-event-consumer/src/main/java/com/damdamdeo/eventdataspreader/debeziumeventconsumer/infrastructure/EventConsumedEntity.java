package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;


import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumed;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumerConsumed;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.KafkaSource;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Table(name = "EventConsumed")
@Entity
@NamedQuery(name = "Events.findByEventId",
        query = "SELECT e FROM EventConsumedEntity e  LEFT JOIN FETCH e.eventConsumerEntities WHERE e.eventId = :eventId")
public class EventConsumedEntity implements EventConsumed {

    @Id
    @Type(type = "pg-uuid")
    private UUID eventId;

    @NotNull
    private Boolean consumed;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "EventConsumerConsumedEntity_eventConsumerId")
    private List<EventConsumerConsumedEntity> eventConsumerEntities;

    @NotNull
    private Integer kafkaPartition;

    @NotNull
    @Column(columnDefinition="TEXT")
    private String kafkaTopic;

    @NotNull
    private Long kafkaOffset;

    public EventConsumedEntity() {}

    public EventConsumedEntity(final UUID eventId, final KafkaSource kafkaSource) {
        this.eventId = Objects.requireNonNull(eventId);
        this.consumed = Boolean.FALSE;
        this.eventConsumerEntities = new ArrayList<>();
        this.kafkaPartition = Objects.requireNonNull(kafkaSource.partition());
        this.kafkaTopic = Objects.requireNonNull(kafkaSource.topic());
        this.kafkaOffset = Objects.requireNonNull(kafkaSource.offset());
    }

    public void addNewEventConsumerConsumed(final Class consumerClass,
                                            final Date consumedAt) {
        eventConsumerEntities.add(
                new EventConsumerConsumedEntity(
                        new EventConsumerId(eventId, consumerClass),
                        consumedAt));
    }

    public void markAsConsumed() {
        consumed = Boolean.TRUE;
    }

    @Override
    public UUID eventId() {
        return eventId;
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
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(consumed, that.consumed) &&
                Objects.equals(eventConsumerEntities, that.eventConsumerEntities) &&
                Objects.equals(kafkaPartition, that.kafkaPartition) &&
                Objects.equals(kafkaTopic, that.kafkaTopic) &&
                Objects.equals(kafkaOffset, that.kafkaOffset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, consumed, eventConsumerEntities, kafkaPartition, kafkaTopic, kafkaOffset);
    }

    @Override
    public String toString() {
        return "EventConsumedEntity{" +
                "eventId=" + eventId +
                ", consumed=" + consumed +
                ", eventConsumerEntities=" + eventConsumerEntities +
                ", kafkaPartition=" + kafkaPartition +
                ", kafkaTopic='" + kafkaTopic + '\'' +
                ", kafkaOffset=" + kafkaOffset +
                '}';
    }
}
