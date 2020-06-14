package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import io.quarkus.arc.AlternativePriority;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class KafkaEventConsumerTest {

    @InjectMock
    SecretStore mockedSecretStore;

    @InjectMock
    KafkaAggregateRootEventConsumedRepository mockedKafkaEventConsumedRepository;

    @InjectMock
    AggregateRootEventPayloadConsumerDeserializer mockedAggregateRootEventPayloadConsumerDeserializer;

    @InjectMock
    AggregateRootEventMetadataConsumerDeserializer mockedAggregateRootEventMetadataConsumerDeSerializer;

    @InjectMock
    AggregateRootMaterializedStateConsumerDeserializer aggregateRootMaterializedStateConsumerDeserializer;

    @InjectMock
    UserTransaction mockedUserTransaction;

    @InjectMock
    CreatedAtProvider mockedCreatedAtProvider;

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject// It is a spy... not supported yet by Quarkus ... maybe in version 1.5.0.Final
            // TODO: use @InjectSpy when available and next remove TestBeanProducers
    AccountDebitedAggregateRootEventConsumer spiedAccountDebitedAggregateRootEventConsumer;

    @BeforeEach
    public void setup() {
        reset(spiedAccountDebitedAggregateRootEventConsumer);
        doReturn(LocalDateTime.of(1980,01,01,0,0,0,0)).when(mockedCreatedAtProvider).createdAt();
        doReturn(Optional.empty()).when(mockedSecretStore).read("AccountAggregateRoot", "damdamdeo");
    }

    @ApplicationScoped
    public static class TestBeanProducers {
        private final AccountDebitedAggregateRootEventConsumer aggregateRootEventConsumer;

        private TestBeanProducers() {
            aggregateRootEventConsumer = spy(new AccountDebitedAggregateRootEventConsumer());
        }

        @Produces
        @AlternativePriority(1)
        public AccountDebitedAggregateRootEventConsumer aggregateRootEventConsumer() {
            return aggregateRootEventConsumer;
        }

    }

    @ApplicationScoped
    public static class AccountDebitedAggregateRootEventConsumer implements AggregateRootEventConsumer {

        @Override
        public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        }

        @Override
        public String aggregateRootType() {
            return "AccountAggregateRoot";
        }

        @Override
        public String eventType() {
            return "AccountDebited";
        }
    }

    @Test
    public void should_consume_event_when_event_has_not_been_consumed() throws Exception {
        // Given
        doReturn(Boolean.FALSE).when(mockedKafkaEventConsumedRepository).hasFinishedConsumingEvent(
                new DebeziumAggregateRootEventId("damdamdeo", "AccountAggregateRoot", 0l));

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        TimeUnit.SECONDS.sleep(1l);// 1 sec should be enough to ensure message has been consumed

        // Then
        // 1569174260987000 in nanoseconds converted to 1569174260987 in milliseconds == Sunday 22 September 2019 17:44:20.987
        final AggregateRootEventConsumable aggregateRootEventConsumable = new DecryptedAggregateRootEventConsumable(
                new DebeziumAggregateRootEventConsumable(
                        new DebeziumAggregateRootEventId("damdamdeo", "AccountAggregateRoot", 0l),
                        LocalDateTime.of(2019, Month.SEPTEMBER, 22, 19, 44, 20, 987000000),
                        "AccountDebited",
                        "{\"@type\": \"UserEventMetadata\", \"executedBy\": \"damdamdeo\"}",
                        "{\"owner\": \"damdamdeo\", \"price\": \"100.00\", \"@type\": \"AccountAggregateAccountDebitedEventPayload\", \"balance\": \"900.00\"}",
                        "{\"@type\": \"AccountAggregateRoot\", \"aggregateRootId\": \"damdamdeo\", \"version\": 1, \"aggregateRootType\": \"AccountAggregateRoot\", \"balance\": \"900.00\"}"
                ),
                Optional.empty(),
                mockedAggregateRootEventMetadataConsumerDeSerializer,
                mockedAggregateRootEventPayloadConsumerDeserializer,
                aggregateRootMaterializedStateConsumerDeserializer);
        verify(spiedAccountDebitedAggregateRootEventConsumer, times(1)).consume(aggregateRootEventConsumable);
        verify(mockedKafkaEventConsumedRepository, times(1)).hasFinishedConsumingEvent(any());
    }

    @Test
    public void should_add_event_as_consumed_after_consuming_it() throws Exception {
        // Given

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        TimeUnit.SECONDS.sleep(1l);// 1 sec should be enough to ensure message has been consumed

        // Then
        verify(mockedKafkaEventConsumedRepository, times(1)).addEventConsumerConsumed(
                eq(new DebeziumAggregateRootEventId("damdamdeo", "AccountAggregateRoot", 0l)),
                eq(spiedAccountDebitedAggregateRootEventConsumer.getClass()),
                eq(LocalDateTime.of(1980,01,01,0,0,0,0)),
                any(ConsumerRecordKafkaInfrastructureMetadata.class),// trop compliqué de tester sur l'offset car celui-ci change en fonction du nombre d'executions
                eq("3bc9898721c64c5d6d17724bf6ec1c715cca0f69"));
    }

    @Test
    public void should_mark_event_as_consumed_after_all_consumers_have_consuming_it() throws Exception {
        // Given

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        TimeUnit.SECONDS.sleep(1l);// 1 sec should be enough to ensure message has been consumed

        // Then
        verify(mockedKafkaEventConsumedRepository, times(1)).markEventAsConsumed(
                eq(new DebeziumAggregateRootEventId("damdamdeo", "AccountAggregateRoot", 0l)),
                eq(LocalDateTime.of(1980,01,01,0,0,0,0)),
                any(ConsumerRecordKafkaInfrastructureMetadata.class)// trop compliqué de tester sur l'offset car celui-ci change en fonction du nombre d'executions
        );
    }

    @Test
    public void should_not_consume_event_when_event_has_already_been_consumed() throws Exception {
        // Given
        doReturn(Boolean.TRUE).when(mockedKafkaEventConsumedRepository).hasFinishedConsumingEvent(
                new DebeziumAggregateRootEventId("damdamdeo", "AccountAggregateRoot", 0l));

        // When
        kafkaDebeziumProducer.produce("eventsourcing/AccountDebited.json");
        TimeUnit.SECONDS.sleep(1l);// 1 sec should be enough to ensure message has been consumed

        // Then
        verify(spiedAccountDebitedAggregateRootEventConsumer, times(0)).consume(any());
    }

}
