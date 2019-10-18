package com.damdamdeo.eventdataspreader.writeside;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountDebitedPayload;
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

public class AccountDebitedPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultEventPayloadsAdapter()));

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(AccountDebitedPayload.class).verify();
    }

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final AccountDebitedPayload accountDebitedPayload = new AccountDebitedPayload("owner", new BigDecimal("100.01"), new BigDecimal("899.99"));

        // When
        final String json = MAPPER.toJson(accountDebitedPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@payloadType\": \"AccountDebitedPayload\", \"@aggregaterootType\": \"AccountAggregate\", \"owner\": \"owner\", \"price\": \"100.01\", \"balance\": \"899.99\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@payloadType\": \"AccountDebitedPayload\", \"@aggregaterootType\": \"AccountAggregate\", \"owner\": \"owner\", \"price\": \"100.01\", \"balance\": \"899.99\"}";

        // When
        final AccountDebitedPayload accountDebitedPayload = (AccountDebitedPayload) MAPPER.fromJson(json, AccountDebitedPayload.class);

        // Then
        assertEquals("owner", accountDebitedPayload.owner());
        assertEquals(new BigDecimal("100.01"), accountDebitedPayload.price());
        assertEquals(new BigDecimal("899.99"), accountDebitedPayload.balance());
    }

}
