package com.damdamdeo.eventsourced.consumer.infra;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.Operation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.DebeziumOperation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.UnsupportedDebeziumOperationException;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInKeyRecord;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInValueRecord;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DebeziumOperationTest {

    @Test
    public void should_create_operation_mapped_with_debezium_create_operation() throws UnsupportedDebeziumOperationException {
        // Given
        final IncomingKafkaRecord<DebeziumJsonbEventInKeyRecord, DebeziumJsonbEventInValueRecord> record = mock(IncomingKafkaRecord.class);
        final DebeziumJsonbEventInValueRecord payload = mock(DebeziumJsonbEventInValueRecord.class);
        doReturn(payload).when(record).getPayload();
        doReturn("c").when(payload).operation();

        // When
        final DebeziumOperation debeziumOperation = DebeziumOperation.fromDebeziumOperation(record);

        // Then
        assertEquals(DebeziumOperation.CREATE_OPERATION, debeziumOperation);
        verify(record, atLeastOnce()).getPayload();
        verify(payload, atLeastOnce()).operation();
    }

    @Test
    public void should_create_operation_mapped_with_create_operation() {
        // Given
        final DebeziumOperation debeziumOperation = DebeziumOperation.CREATE_OPERATION;

        // When && Then
        assertEquals(Operation.CREATE, debeziumOperation.operation());
    }

    @Test
    public void should_read_due_to_snapshotting_operation_mapped_with_debezium_read_due_to_snapshotting_operation() throws UnsupportedDebeziumOperationException {
        // Given
        final IncomingKafkaRecord<DebeziumJsonbEventInKeyRecord, DebeziumJsonbEventInValueRecord> record = mock(IncomingKafkaRecord.class);
        final DebeziumJsonbEventInValueRecord payload = mock(DebeziumJsonbEventInValueRecord.class);
        doReturn(payload).when(record).getPayload();
        doReturn("r").when(payload).operation();

        // When
        final DebeziumOperation debeziumOperation = DebeziumOperation.fromDebeziumOperation(record);

        // Then
        assertEquals(DebeziumOperation.READ_DUE_TO_SNAPSHOTTING_AT_CONNECTOR_START, debeziumOperation);
        verify(record, atLeastOnce()).getPayload();
        verify(payload, atLeastOnce()).operation();
    }

    @Test
    public void should_read_due_to_snapshotting_operation_mapped_with_read_operation() {
        // Given
        final DebeziumOperation debeziumOperation = DebeziumOperation.READ_DUE_TO_SNAPSHOTTING_AT_CONNECTOR_START;

        // When && Then
        assertEquals(Operation.READ, debeziumOperation.operation());
    }

    @Test
    public void should_throw_unsupported_debezium_operation_exception_when_debezium_operation_is_unsupported() {
        // Given
        final IncomingKafkaRecord<DebeziumJsonbEventInKeyRecord, DebeziumJsonbEventInValueRecord> record = mock(IncomingKafkaRecord.class);
        final DebeziumJsonbEventInValueRecord payload = mock(DebeziumJsonbEventInValueRecord.class);
        doReturn(payload).when(record).getPayload();
        doReturn("o").when(payload).operation();
        doReturn(0).when(record).getPartition();
        doReturn("topic").when(record).getTopic();
        doReturn(1l).when(record).getOffset();
        final DebeziumJsonbEventInKeyRecord key = mock(DebeziumJsonbEventInKeyRecord.class);
        doReturn(key).when(record).getKey();
        doReturn(payload).when(record).getPayload();

        // When && Then
        assertThatThrownBy(() -> DebeziumOperation.fromDebeziumOperation(record))
                .isInstanceOf(UnsupportedDebeziumOperationException.class)
                .hasFieldOrPropertyWithValue("partition", 0)
                .hasFieldOrPropertyWithValue("topic", "topic")
                .hasFieldOrPropertyWithValue("offset", 1l)
                .hasFieldOrPropertyWithValue("key", key)
                .hasFieldOrPropertyWithValue("payload", payload)
                ;
        verify(record, atLeastOnce()).getPayload();
        verify(payload, atLeastOnce()).operation();
        verify(record, atLeastOnce()).getPartition();
        verify(record, atLeastOnce()).getTopic();
        verify(record, atLeastOnce()).getOffset();
        verify(record, atLeastOnce()).getKey();
        verify(record, atLeastOnce()).getPayload();
    }

}
