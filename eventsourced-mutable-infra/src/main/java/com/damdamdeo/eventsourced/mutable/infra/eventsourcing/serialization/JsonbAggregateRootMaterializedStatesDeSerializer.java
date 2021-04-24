package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.JsonbCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.eventsourcing.UnsupportedAggregateRoot;
import com.damdamdeo.eventsourced.mutable.eventsourcing.serialization.AggregateRootMaterializedStatesDeSerializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Objects;

@ApplicationScoped
public class JsonbAggregateRootMaterializedStatesDeSerializer implements AggregateRootMaterializedStatesDeSerializer {

    final Instance<JsonbAggregateRootMaterializedStateDeSerializer> jsonbAggregateRootMaterializedStateDeSerializerBeans;
    final JsonbCryptoService jsonbCryptoService;

    public JsonbAggregateRootMaterializedStatesDeSerializer(@Any final Instance<JsonbAggregateRootMaterializedStateDeSerializer> jsonbAggregateRootMaterializedStateDeSerializerBeans,
                                                            final JsonbCryptoService jsonbCryptoService) {
        this.jsonbAggregateRootMaterializedStateDeSerializerBeans = Objects.requireNonNull(jsonbAggregateRootMaterializedStateDeSerializerBeans);
        this.jsonbCryptoService = jsonbCryptoService;
    }

    @Override
    public String serialize(final AggregateRoot aggregateRoot, final boolean shouldEncrypt) {
        final String aggregateRootType = aggregateRoot.aggregateRootType();
        return jsonbAggregateRootMaterializedStateDeSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .findFirst()
                .map(jsonbAggregateRootMaterializedStateDeSerializerBean -> jsonbAggregateRootMaterializedStateDeSerializerBean.serialize(aggregateRoot, shouldEncrypt))
                .map(jsonValue -> jsonValue.toString())
                .orElseThrow(() -> new UnsupportedAggregateRoot(aggregateRootType));
    }

    @Override
    public <T extends AggregateRoot> T deserialize(final AggregateRootMaterializedState aggregateRootMaterializedState) {
        final String aggregateRootType = aggregateRootMaterializedState.aggregateRootId().aggregateRootType();
        return (T) jsonbAggregateRootMaterializedStateDeSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .findFirst()
                .map(jsonbAggregateRootMaterializedStateDeSerializerBean -> {
                    final JsonObject jsonObject = Json.createReader(new StringReader(aggregateRootMaterializedState.serializedMaterializedState())).readObject();
                    final JsonObject decryptedJsonObject = this.jsonbCryptoService.recursiveDecrypt(jsonObject);
                    return jsonbAggregateRootMaterializedStateDeSerializerBean.deserialize(aggregateRootMaterializedState.aggregateRootId(), decryptedJsonObject, aggregateRootMaterializedState.version());
                })
                .orElseThrow(() -> new UnsupportedAggregateRoot(aggregateRootType));
    }

}
