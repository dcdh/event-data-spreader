package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

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
    final ObjectMapper objectMapper;

    public JacksonAggregateRootEventPayloadsDeSerializer(@Any final Instance<JacksonAggregateRootEventPayloadDeSerializer> jacksonAggregateRootEventPayloadDeSerializerBeans) {
        this.jacksonAggregateRootEventPayloadDeSerializerBeans = Objects.requireNonNull(jacksonAggregateRootEventPayloadDeSerializerBeans);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String serialize(final String aggregateRootType,
                            final String eventType,
                            final AggregateRootEventPayload aggregateRootEventPayload) throws UnsupportedAggregateRootEventPayload {
        return jacksonAggregateRootEventPayloadDeSerializerBeans.stream()
                .filter(bean -> aggregateRootType.equals(bean.aggregateRootType()))
                .filter(bean -> eventType.equals(bean.eventType()))
                .findFirst()
                .map(jacksonAggregateRootEventPayloadDeSerializerBean -> jacksonAggregateRootEventPayloadDeSerializerBean.encode(aggregateRootEventPayload, this.objectMapper))
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
                        return jacksonAggregateRootEventPayloadDeSerializerBean.decode(json);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new UnsupportedAggregateRootEventPayload(aggregateRootType, eventType));
    }

}
