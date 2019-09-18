package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuilderInitializer;

public class EventSourcingMetadataBuilderInitializer implements MetadataBuilderInitializer {

    @Override
    public void contribute(final MetadataBuilder metadataBuilder, final StandardServiceRegistry serviceRegistry) {
        metadataBuilder.applyBasicType(JSONBAggregateRootUserType.INSTANCE, "jsonbAggregateRoot");
        metadataBuilder.applyBasicType(JSONBEventPayloadUserType.INSTANCE, "jsonbEventPayload");
        metadataBuilder.applyBasicType(JSONBEventMetadataUserType.INSTANCE, "jsonbEventMetaData");
    }

}
