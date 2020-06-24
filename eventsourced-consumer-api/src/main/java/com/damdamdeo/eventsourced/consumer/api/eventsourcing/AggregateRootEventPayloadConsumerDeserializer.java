package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Secret;

public interface AggregateRootEventPayloadConsumerDeserializer {

    AggregateRootEventPayloadConsumer deserialize(Secret secret, String eventPayload);

}
