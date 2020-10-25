package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.JsonbCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadsDeSerializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Objects;

@ApplicationScoped
public class JsonbAggregateRootEventPayloadsDeSerializer implements AggregateRootEventPayloadsDeSerializer {

    final Instance<JsonbAggregateRootEventPayloadDeSerializer> jacksonAggregateRootEventPayloadDeSerializerBeans;
    final JsonbCryptoService jsonbCryptoService;

    public JsonbAggregateRootEventPayloadsDeSerializer(@Any final Instance<JsonbAggregateRootEventPayloadDeSerializer> jacksonAggregateRootEventPayloadDeSerializerBeans,
                                                       final JsonbCryptoService jsonbCryptoService) {
        this.jacksonAggregateRootEventPayloadDeSerializerBeans = Objects.requireNonNull(jacksonAggregateRootEventPayloadDeSerializerBeans);
        this.jsonbCryptoService = Objects.requireNonNull(jsonbCryptoService);
    }

    @Override
    public String serialize(final AggregateRootId aggregateRootId,
                            final String eventType,
                            final AggregateRootEventPayload aggregateRootEventPayload) throws UnsupportedAggregateRootEventPayload {
        final String aggregateRootType = aggregateRootId.aggregateRootType();
        return jacksonAggregateRootEventPayloadDeSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .filter(bean -> eventType.equals(bean.eventType()))
                .findFirst()
                .map(jsonbAggregateRootEventPayloadDeSerializerBean ->
                        jsonbAggregateRootEventPayloadDeSerializerBean.encode(aggregateRootId, aggregateRootEventPayload))
                .map(jsonValue -> jsonValue.toString())
                .orElseThrow(() -> new UnsupportedAggregateRootEventPayload(aggregateRootType, eventType));
    }

    @Override
    public AggregateRootEventPayload deserialize(final String aggregateRootType,
                                                 final String eventType,
                                                 final String eventPayload) throws UnsupportedAggregateRootEventPayload {
        return jacksonAggregateRootEventPayloadDeSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .filter(bean -> eventType.equals(bean.eventType()))
                .findFirst()
                .map(jsonbAggregateRootEventPayloadDeSerializerBean -> {
                    final JsonObject jsonObject = Json.createReader(new StringReader(eventPayload)).readObject();
                    final JsonObject decryptedJsonObject = this.jsonbCryptoService.recursiveDecrypt(jsonObject);
                    return jsonbAggregateRootEventPayloadDeSerializerBean.decode(decryptedJsonObject);
                })
                .orElseThrow(() -> new UnsupportedAggregateRootEventPayload(aggregateRootType, eventType));
    }

}
