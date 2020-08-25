package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.CryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootMaterializedStatesDeSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import java.util.Objects;

@ApplicationScoped
public class JacksonAggregateRootMaterializedStatesDeSerializer implements AggregateRootMaterializedStatesDeSerializer {

    final Instance<JacksonAggregateRootMaterializedStateDeSerializer> jacksonAggregateRootMaterializedStateDeSerializerBeans;
    final CryptoService<JsonNode> cryptoService;
    final ObjectMapper objectMapper;

    public JacksonAggregateRootMaterializedStatesDeSerializer(@Any final Instance<JacksonAggregateRootMaterializedStateDeSerializer> jacksonAggregateRootMaterializedStateDeSerializerBeans,
                                                              final CryptoService<JsonNode> cryptoService) {
        this.jacksonAggregateRootMaterializedStateDeSerializerBeans = Objects.requireNonNull(jacksonAggregateRootMaterializedStateDeSerializerBeans);
        this.cryptoService = cryptoService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String serialize(final AggregateRoot aggregateRoot, final boolean shouldEncrypt) {
        final String aggregateRootType = aggregateRoot.aggregateRootType();
        return jacksonAggregateRootMaterializedStateDeSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .findFirst()
                .map(jacksonAggregateRootMaterializedStateDeSerializerBean -> jacksonAggregateRootMaterializedStateDeSerializerBean.serialize(aggregateRoot, shouldEncrypt, this.objectMapper))
                .map(jsonNode -> {
                    try {
                        return objectMapper.writeValueAsString(jsonNode);
                    } catch (final JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new UnsupportedAggregateRoot(aggregateRootType));
    }

    @Override
    public <T extends AggregateRoot> T deserialize(final AggregateRootMaterializedState aggregateRootMaterializedState) {
        final String aggregateRootType = aggregateRootMaterializedState.aggregateRootId().aggregateRootType();
        return (T) jacksonAggregateRootMaterializedStateDeSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .findFirst()
                .map(jacksonAggregateRootMaterializedStateDeSerializerBean -> {
                    try {
                        final JsonNode aggregateRoot = objectMapper.readTree(aggregateRootMaterializedState.serializedMaterializedState());
                        cryptoService.recursiveDecrypt(aggregateRoot);
                        return jacksonAggregateRootMaterializedStateDeSerializerBean.deserialize(aggregateRootMaterializedState.aggregateRootId(), aggregateRoot, aggregateRootMaterializedState.version());
                    } catch (final JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new UnsupportedAggregateRoot(aggregateRootType));
    }

}
