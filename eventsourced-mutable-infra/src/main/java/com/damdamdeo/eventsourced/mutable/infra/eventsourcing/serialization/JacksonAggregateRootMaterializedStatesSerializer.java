package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootMaterializedStatesSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import java.util.Objects;

@ApplicationScoped
public class JacksonAggregateRootMaterializedStatesSerializer implements AggregateRootMaterializedStatesSerializer {

    final Instance<JacksonAggregateRootMaterializedStateSerializer> jacksonAggregateRootMaterializedStateSerializerBeans;
    final ObjectMapper objectMapper;

    public JacksonAggregateRootMaterializedStatesSerializer(@Any final Instance<JacksonAggregateRootMaterializedStateSerializer> jacksonAggregateRootMaterializedStateSerializerBeans) {
        this.jacksonAggregateRootMaterializedStateSerializerBeans = Objects.requireNonNull(jacksonAggregateRootMaterializedStateSerializerBeans);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String serialize(final AggregateRoot aggregateRoot, final Secret secret, final boolean shouldEncrypt) {
        final String aggregateRootType = aggregateRoot.aggregateRootType();
        return jacksonAggregateRootMaterializedStateSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .findFirst()
                .map(jacksonAggregateRootMaterializedStateSerializerBean -> jacksonAggregateRootMaterializedStateSerializerBean.encode(aggregateRoot, secret, shouldEncrypt, this.objectMapper))
                .map(jsonNode -> {
                    try {
                        return objectMapper.writeValueAsString(jsonNode);
                    } catch (final JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new UnsupportedAggregateRoot(aggregateRootType));
    }

}
