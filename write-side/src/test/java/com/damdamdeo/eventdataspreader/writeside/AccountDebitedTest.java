package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountDebited;
import com.damdamdeo.eventdataspreader.writeside.user.type.DefaultEventPayloadsAdapter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountDebitedTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultEventPayloadsAdapter()));

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(AccountDebited.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final AccountDebited accountDebited = new AccountDebited("owner", new BigDecimal("100.01"), new BigDecimal("899.99"));

        // When
        final String json = MAPPER.toJson(accountDebited);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"AccountDebited\", \"owner\": \"owner\", \"price\": \"100.01\", \"balance\": \"899.99\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"AccountDebited\", \"owner\": \"owner\", \"price\": \"100.01\", \"balance\": \"899.99\"}";

        // When
        final AccountDebited accountDebited = (AccountDebited) MAPPER.fromJson(json, AccountDebited.class);

        // Then
        assertEquals("owner", accountDebited.owner());
        assertEquals(new BigDecimal("100.01"), accountDebited.price());
        assertEquals(new BigDecimal("899.99"), accountDebited.balance());
    }

}
