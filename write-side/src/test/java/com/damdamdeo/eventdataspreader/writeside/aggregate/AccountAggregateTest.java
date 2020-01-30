package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled// FIXME Fail to build using maven !!!
public class AccountAggregateTest {

    private static class DefaultJacksonAggregateRootSubtypes implements JacksonAggregateRootSubtypes {

        @Override
        public List<JacksonSubtype<AggregateRoot>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(AccountAggregate.class, "AccountAggregate"));
        }

    }

    private static class DefaultEncryptedEventSecret implements EncryptedEventSecret {

        @Override
        public String aggregateRootId() {
            return null;
        }

        @Override
        public String aggregateRootType() {
            return null;
        }

        @Override
        public Date creationDate() {
            return null;
        }

        @Override
        public String secret() {
            return null;
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootSerializer aggregateRootSerializer = new JacksonAggregateRootSerializer(new DefaultJacksonAggregateRootSubtypes());
        final AccountAggregate accountAggregate = new AccountAggregate();
        accountAggregate.handle(new DebitAccountCommand("owner", BigDecimal.TEN, "executedBy"));

        // When
        final String serialized = aggregateRootSerializer.serialize(new DefaultEncryptedEventSecret(), accountAggregate);

        // Then
        assertEquals("{\"@type\":\"AccountAggregate\",\"aggregateRootId\":\"owner\",\"owner\":\"owner\",\"balance\":990,\"version\":0,\"aggregateRootType\":\"AccountAggregate\"}", serialized);
    }

}
