package com.damdamdeo.eventsourced.encryption.api;

// Add to create an interface with infra (jsonb) due to this issue when testing in KafkaEventConsumerTest
// https://github.com/quarkusio/quarkus/issues/12761

import javax.json.JsonObject;
import javax.json.JsonValue;

public interface JsonbCryptoService extends CryptoService<JsonValue, JsonObject> {
}
