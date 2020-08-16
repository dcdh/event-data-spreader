package com.damdamdeo.eventsourced.encryption.infra.jackson;

import com.damdamdeo.eventsourced.encryption.api.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

// It is not possible to call the parent of ObjectNode or JsonNode
// https://github.com/FasterXML/jackson-databind/issues/2250
@ApplicationScoped
public class JsonCryptoService implements CryptService<JsonNode> {

    private static final String STRING = "string";
    private static final String INTEGER = "integer";
    private static final String LONG = "long";
    private static final String BIG_DECIMAL = "bigDecimal";
    private static final String BIG_INTEGER = "bigInteger";

    public static final String ENCRYPTED = "encrypted";
    public static final String AGGREGATE_ROOT_TYPE = "aggregateRootType";
    public static final String AGGREGATE_ROOT_ID = "aggregateRootId";
    public static final String TYPE = "type";

    private final SecretStore secretStore;
    private final ObjectMapper objectMapper;

    public JsonCryptoService(final SecretStore secretStore) {
        this.secretStore = Objects.requireNonNull(secretStore);
        this.objectMapper = new ObjectMapper();
    }

    // {'firstName': {'encrypted': 'XXXXX', 'aggregateRootType': 'TodoAggregateRoot', 'aggregateRootId': 'damdamdeo', 'type': 'string'} integer...
    // {'firstName': 'damdamdeo'}
    // le soucis c'est le decryptage : ***** string 0 integer

    enum Type {

        STRING {
            @Override
            String type() {
                return JsonCryptoService.STRING;
            }

            @Override
            Object mapFromString(final String value) {
                return value;
            }

            @Override
            Object anonymizedValue() {
                return "*****";
            }

            @Override
            boolean isObjectNodeType(final JsonNode jsonNode) {
                return jsonNode.isTextual();
            }
        },

        INTEGER {
            @Override
            String type() {
                return JsonCryptoService.INTEGER;
            }

            @Override
            Object mapFromString(final String value) {
                return Integer.valueOf(value);
            }

            @Override
            Object anonymizedValue() {
                return 0;
            }

            @Override
            boolean isObjectNodeType(final JsonNode jsonNode) {
                return jsonNode.isInt();
            }
        },

        LONG {
            @Override
            String type() {
                return JsonCryptoService.LONG;
            }

            @Override
            Object mapFromString(final String value) {
                return Long.valueOf(value);
            }

            @Override
            Object anonymizedValue() {
                return 0L;
            }

            @Override
            boolean isObjectNodeType(final JsonNode jsonNode) {
                return jsonNode.isLong();
            }
        },

        BIG_DECIMAL {
            @Override
            String type() {
                return JsonCryptoService.BIG_DECIMAL;
            }

            @Override
            Object mapFromString(final String value) {
                return new BigDecimal(value);
            }

            @Override
            Object anonymizedValue() {
                return new BigDecimal("0");
            }

            @Override
            boolean isObjectNodeType(final JsonNode jsonNode) {
                return jsonNode.isBigDecimal();
            }
        },

        BIG_INTEGER {
            @Override
            String type() {
                return JsonCryptoService.BIG_INTEGER;
            }

            @Override
            Object mapFromString(final String value) {
                return new BigInteger(value);
            }

            @Override
            Object anonymizedValue() {
                return new BigInteger("0");
            }

            @Override
            boolean isObjectNodeType(final JsonNode jsonNode) {
                return jsonNode.isBigInteger();
            }
        };

        abstract String type();

        abstract Object mapFromString(String value);

        abstract Object anonymizedValue();

        abstract boolean isObjectNodeType(JsonNode jsonNode);

        public static Type guessFromType(final String givenType) {
            return Arrays.stream(Type.values())
                    .filter(type -> givenType.equals(type.type()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException());
        }

        public static Type guessFromObjectNode(final JsonNode jsonNode) {
            return Arrays.stream(Type.values())
                    .filter(type -> type.isObjectNodeType(jsonNode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException());
        }
    }
// AESEncryption or NoEncryption !
    @Override
    public void encrypt(final AggregateRootId aggregateRootId,
                        final JsonNode parentNode,
                        final String fieldName,
                        final Encryption encryption) throws UnableToEncryptMissingSecretException {
        if (parentNode.isObject()) {
            final JsonNode targetJsonNode = parentNode.get(fieldName);
            final Type type = Type.guessFromObjectNode(targetJsonNode);
            Optional.of(secretStore.read(aggregateRootId))
                    .map(secret -> secret.encrypt(aggregateRootId, targetJsonNode.asText(), encryption))
                    .map(encryptedValue -> {
                        final ObjectNode encrypted = objectMapper.createObjectNode();
                        encrypted.set(ENCRYPTED, objectMapper.convertValue(encryptedValue, JsonNode.class));
                        encrypted.set(AGGREGATE_ROOT_TYPE, objectMapper.convertValue(aggregateRootId.aggregateRootType(), JsonNode.class));
                        encrypted.set(AGGREGATE_ROOT_ID, objectMapper.convertValue(aggregateRootId.aggregateRootId(), JsonNode.class));
                        encrypted.set(TYPE, objectMapper.convertValue(type.type(), JsonNode.class));
                        return encrypted;
                    })
                    .map(encrypted -> ((ObjectNode) parentNode).replace(fieldName, encrypted));
        }
    }

    @Override
    public void decrypt(final JsonNode parentNode,
                        final String fieldName,
                        final Encryption encryption) {
        if (parentNode.isObject()) {
            final JsonNode targetJsonNode = parentNode.get(fieldName);
            if (targetJsonNode.isObject() && targetJsonNode.has(ENCRYPTED)) {
                final Type type = Type.guessFromType(targetJsonNode.get(TYPE).asText());
                final AggregateRootId aggregateRootId = new JacksonAggregateRootId(targetJsonNode);
                Optional.of(secretStore.read(aggregateRootId))
                        .map(secret -> {
                            try {
                                final String decryptedValue = secret.decrypt(aggregateRootId, targetJsonNode.get(ENCRYPTED).asText(), encryption);
                                return type.mapFromString(decryptedValue);
                            } catch (final UnableToDecryptMissingSecretException e) {
                                return type.anonymizedValue();
                            }
                        }).map(value -> ((ObjectNode) parentNode).set(fieldName, objectMapper.convertValue(value, JsonNode.class)));
            }
        }
    }

    @Override
    public void recursiveDecrypt(final JsonNode jsonNode, final Encryption encryption) {
        if (jsonNode.isObject()) {
            final Iterator<String> fieldsNameIterator = jsonNode.fieldNames();
            while (fieldsNameIterator.hasNext()) {
                final String fieldName = fieldsNameIterator.next();
                decrypt(jsonNode, fieldName, encryption);
                recursiveDecrypt(jsonNode.get(fieldName), encryption);
            }
        } else if (jsonNode.isArray()) {
            final Iterator<JsonNode> jsonNodeIterator = jsonNode.elements();
            while (jsonNodeIterator.hasNext()) {
                final JsonNode childJsonNode = jsonNodeIterator.next();
                recursiveDecrypt(childJsonNode, encryption);
            }
        } else {
            // do nothing
        }
    }

}
