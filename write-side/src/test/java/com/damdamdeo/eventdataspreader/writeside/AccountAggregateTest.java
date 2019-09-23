package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.user.type.DefaultAggregateRootAdapter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountAggregateTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultAggregateRootAdapter()));

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final AccountAggregate accountAggregate = new AccountAggregate("aggregateRootId",
                "owner",
                new BigDecimal("100.01"),
                1L);

        // When
        final String json = MAPPER.toJson(accountAggregate);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"AccountAggregate\", \"aggregateRootId\": \"aggregateRootId\", \"owner\": \"owner\", \"balance\": \"100.01\", \"version\": 1}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"AccountAggregate\", \"aggregateRootId\": \"aggregateRootId\", \"owner\": \"owner\", \"balance\": \"100.01\", \"version\": 1}";

        // When
        final AccountAggregate accountAggregate = (AccountAggregate) MAPPER.fromJson(json, AccountAggregate.class);

        // Then
        assertEquals("aggregateRootId", accountAggregate.aggregateRootId());
        assertEquals("owner", accountAggregate.owner());
        assertEquals(new BigDecimal("100.01"), accountAggregate.balance());
        assertEquals(1l, accountAggregate.version());
    }

}
