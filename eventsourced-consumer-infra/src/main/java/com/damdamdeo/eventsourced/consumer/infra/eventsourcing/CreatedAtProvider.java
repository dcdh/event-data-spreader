package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ApplicationScoped
public class CreatedAtProvider {

    public LocalDateTime createdAt() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

}
