package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata.ConnectedUserMetadataEnhancer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata.MetadataEnhancerContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultAggregateRootEventMetadataDeSerializer implements AggregateRootEventMetadataDeSerializer {

    final ObjectMapper objectMapper;

    public DefaultAggregateRootEventMetadataDeSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String serialize() {
        final ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(ConnectedUserMetadataEnhancer.USER_ANONYMOUS,
                (Boolean) MetadataEnhancerContextHolder.get(ConnectedUserMetadataEnhancer.USER_ANONYMOUS));
        objectNode.put(ConnectedUserMetadataEnhancer.USER_NAME,
                (String) MetadataEnhancerContextHolder.get(ConnectedUserMetadataEnhancer.USER_NAME));
        try {
            return objectMapper.writeValueAsString(objectNode);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
