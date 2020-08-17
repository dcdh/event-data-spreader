package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.AESEncryptionQualifier;
import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadsDeSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import java.util.Objects;

@ApplicationScoped
public class JacksonAggregateRootEventPayloadsDeSerializer implements AggregateRootEventPayloadsDeSerializer {

    final Instance<JacksonAggregateRootEventPayloadDeSerializer> jacksonAggregateRootEventPayloadDeSerializerBeans;
    final CryptoService<JsonNode> jsonCryptoService;
    final Encryption encryption;
    final ObjectMapper objectMapper;

    public JacksonAggregateRootEventPayloadsDeSerializer(@Any final Instance<JacksonAggregateRootEventPayloadDeSerializer> jacksonAggregateRootEventPayloadDeSerializerBeans,
                                                         final CryptoService<JsonNode> jsonCryptoService,
                                                         @AESEncryptionQualifier final Encryption encryption) {
        this.jacksonAggregateRootEventPayloadDeSerializerBeans = Objects.requireNonNull(jacksonAggregateRootEventPayloadDeSerializerBeans);
        this.objectMapper = new ObjectMapper();
        this.jsonCryptoService = jsonCryptoService;
        this.encryption = encryption;
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
                .map(jacksonAggregateRootEventPayloadDeSerializerBean ->
                        jacksonAggregateRootEventPayloadDeSerializerBean.encode(aggregateRootId, aggregateRootEventPayload, this.objectMapper))
                .map(jsonNode -> {
                    try {
                        return objectMapper.writeValueAsString(jsonNode);
                    } catch (final JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
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
                .map(jacksonAggregateRootEventPayloadDeSerializerBean -> {
                    try {
                        final JsonNode json = objectMapper.readTree(eventPayload);
                        this.jsonCryptoService.recursiveDecrypt(json, encryption);
                        return jacksonAggregateRootEventPayloadDeSerializerBean.decode(json);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new UnsupportedAggregateRootEventPayload(aggregateRootType, eventType));
    }

}
