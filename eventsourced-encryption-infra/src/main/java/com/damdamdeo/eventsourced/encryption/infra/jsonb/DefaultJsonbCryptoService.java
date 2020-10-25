package com.damdamdeo.eventsourced.encryption.infra.jsonb;

import com.damdamdeo.eventsourced.encryption.api.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import javax.enterprise.context.ApplicationScoped;
import javax.json.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class DefaultJsonbCryptoService implements JsonbCryptoService {

    public static final String ENCRYPTED = "encrypted";
    public static final String AGGREGATE_ROOT_TYPE = "aggregateRootType";
    public static final String AGGREGATE_ROOT_ID = "aggregateRootId";
    public static final String ENCRYPTED_TYPE = "type";

    private final SecretStore secretStore;
    private final Encryption encryption;

    public DefaultJsonbCryptoService(final SecretStore secretStore, @AESEncryptionQualifier final Encryption encryption) {
        this.secretStore = Objects.requireNonNull(secretStore);
        this.encryption = Objects.requireNonNull(encryption);
    }

    public enum EncryptedType {

        STRING {

            @Override
            String type() {
                return "string";
            }

            @Override
            JsonValue mapJsonValueFromString(final String value) {
                return Json.createValue(value);
            }

            @Override
            JsonValue anonymizedJsonValue() {
                return Json.createValue("*****");
            }

        },

        INTEGER {


            @Override
            String type() {
                return "integer";
            }

            @Override
            JsonValue mapJsonValueFromString(final String value) {
                return Json.createValue(Integer.valueOf(value));
            }

            @Override
            JsonValue anonymizedJsonValue() {
                return Json.createValue(0);
            }

        },

        LONG {

            @Override
            String type() {
                return "long";
            }

            @Override
            JsonValue mapJsonValueFromString(final String value) {
                return Json.createValue(Long.valueOf(value));
            }

            @Override
            JsonValue anonymizedJsonValue() {
                return Json.createValue(0l);
            }

        },

        BIG_DECIMAL {

            @Override
            String type() {
                return "bigDecimal";
            }

            @Override
            JsonValue mapJsonValueFromString(final String value) {
                return Json.createValue(new BigDecimal(value));
            }

            @Override
            JsonValue anonymizedJsonValue() {
                return Json.createValue(BigDecimal.ZERO);
            }

        },

        BIG_INTEGER {

            @Override
            String type() {
                return "bigInteger";
            }

            @Override
            JsonValue mapJsonValueFromString(final String value) {
                return Json.createValue(new BigInteger(value));
            }

            @Override
            JsonValue anonymizedJsonValue() {
                return Json.createValue(BigInteger.ZERO);
            }

        };

        abstract String type();

        abstract JsonValue mapJsonValueFromString(String value);

        abstract JsonValue anonymizedJsonValue();

        public static EncryptedType guessFromEncryptedType(final String givenType) {
            return Arrays.stream(EncryptedType.values())
                    .filter(encryptedType -> givenType.equals(encryptedType.type()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException());
        }

    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final String valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        if (shouldEncrypt) {
            final String encryptedValue = secretStore.read(aggregateRootId).encrypt(aggregateRootId, valueToEncrypt, encryption);
            return Json.createObjectBuilder()
                    .add(ENCRYPTED, encryptedValue)
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootId.aggregateRootType())
                    .add(AGGREGATE_ROOT_ID, aggregateRootId.aggregateRootId())
                    .add(ENCRYPTED_TYPE, EncryptedType.STRING.type())
                    .build();
        }
        return Json.createValue(valueToEncrypt);
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final Long valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        if (shouldEncrypt) {
            final String encryptedValue = secretStore.read(aggregateRootId).encrypt(aggregateRootId, valueToEncrypt.toString(), encryption);
            return Json.createObjectBuilder()
                    .add(ENCRYPTED, encryptedValue)
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootId.aggregateRootType())
                    .add(AGGREGATE_ROOT_ID, aggregateRootId.aggregateRootId())
                    .add(ENCRYPTED_TYPE, EncryptedType.LONG.type())
                    .build();
        }
        return Json.createValue(valueToEncrypt);
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final Integer valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        if (shouldEncrypt) {
            final String encryptedValue = secretStore.read(aggregateRootId).encrypt(aggregateRootId, valueToEncrypt.toString(), encryption);
            return Json.createObjectBuilder()
                    .add(ENCRYPTED, encryptedValue)
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootId.aggregateRootType())
                    .add(AGGREGATE_ROOT_ID, aggregateRootId.aggregateRootId())
                    .add(ENCRYPTED_TYPE, EncryptedType.INTEGER.type())
                    .build();
        }
        return Json.createValue(valueToEncrypt);
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final BigInteger valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        if (shouldEncrypt) {
            final String encryptedValue = secretStore.read(aggregateRootId).encrypt(aggregateRootId, valueToEncrypt.toString(), encryption);
            return Json.createObjectBuilder()
                    .add(ENCRYPTED, encryptedValue)
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootId.aggregateRootType())
                    .add(AGGREGATE_ROOT_ID, aggregateRootId.aggregateRootId())
                    .add(ENCRYPTED_TYPE, EncryptedType.BIG_INTEGER.type())
                    .build();
        }
        return Json.createValue(valueToEncrypt);
    }

    @Override
    public JsonValue encrypt(final AggregateRootId aggregateRootId, final BigDecimal valueToEncrypt, final boolean shouldEncrypt) throws UnableToEncryptMissingSecretException {
        if (shouldEncrypt) {
            final String encryptedValue = secretStore.read(aggregateRootId).encrypt(aggregateRootId, valueToEncrypt.toString(), encryption);
            return Json.createObjectBuilder()
                    .add(ENCRYPTED, encryptedValue)
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootId.aggregateRootType())
                    .add(AGGREGATE_ROOT_ID, aggregateRootId.aggregateRootId())
                    .add(ENCRYPTED_TYPE, EncryptedType.BIG_DECIMAL.type())
                    .build();
        }
        return Json.createValue(valueToEncrypt);
    }

    @Override
    public JsonValue decrypt(final JsonValue valueDecryptable) {
        return Optional.of(valueDecryptable)
                .filter(decryptable -> JsonValue.ValueType.OBJECT.equals(decryptable.getValueType()))
                .map(JsonValue::asJsonObject)
                .filter(decryptable -> decryptable.containsKey(ENCRYPTED))
                .map(decryptable -> {
                    final AggregateRootId aggregateRootId = new JsonObjectEncryptedAggregateRootId(decryptable);
                    return Optional.of(secretStore.read(aggregateRootId))
                            .map(secret -> {
                                final EncryptedType encryptedType = EncryptedType.guessFromEncryptedType(decryptable.getString(ENCRYPTED_TYPE));
                                try {
                                    final String decryptedValue = secret.decrypt(aggregateRootId, decryptable.getString(ENCRYPTED), encryption);
                                    return encryptedType.mapJsonValueFromString(decryptedValue);
                                } catch (final UnableToDecryptMissingSecretException e) {
                                    return encryptedType.anonymizedJsonValue();
                                }
                            })
                            .get();
                })
                .orElse(valueDecryptable);
    }

    @Override
    public JsonObject recursiveDecrypt(final JsonObject source) {
        return recursiveDecrypt(source, Json.createObjectBuilder())
                .build();
    }

    private JsonObjectBuilder recursiveDecrypt(final JsonValue jsonValue, final JsonObjectBuilder jsonObjectBuilder) {
        if (isJsonValueIsAnObject(jsonValue)) {
            final Set<String> propertiesName = jsonValue.asJsonObject().keySet();
            for (final String propertyName : propertiesName) {
                final JsonObject parentJsonObject = jsonValue.asJsonObject();
                final JsonValue propertyJsonValue = parentJsonObject.get(propertyName);
                if (isJsonValueIsAnObject(propertyJsonValue)) {
                    // Object property case
                    if (isPropertyIsEncrypted(parentJsonObject, propertyName)) {
                        // decrypt property and clone it by adding it
                        jsonObjectBuilder.add(propertyName, decrypt(propertyJsonValue));
                    } else {
                        // recursive decrypt
                        final JsonObjectBuilder propertyJsonObjectBuilder = Json.createObjectBuilder();
                        recursiveDecrypt(propertyJsonValue, propertyJsonObjectBuilder);
                        jsonObjectBuilder.add(propertyName, propertyJsonObjectBuilder);
                    }
                } else if (isJsonValueIsAnArray(propertyJsonValue)) {
                    // Array property case
                    final JsonArrayBuilder propertyJsonArrayBuilder = Json.createArrayBuilder();
                    propertyJsonValue.asJsonArray()
                            .forEach(propertyJsonValueItem -> {
                                final JsonObjectBuilder propertyJsonObjectBuilder = Json.createObjectBuilder();
                                propertyJsonArrayBuilder.add(recursiveDecrypt(propertyJsonValueItem, propertyJsonObjectBuilder));
                                propertyJsonObjectBuilder.build();
                            });
                    jsonObjectBuilder.add(propertyName, propertyJsonArrayBuilder.build());
                } else {
                    // it is a value clone it by adding it
                    jsonObjectBuilder.add(propertyName, propertyJsonValue);
                }
            }
        }
        return jsonObjectBuilder;
    }

    private Boolean isPropertyIsEncrypted(final JsonObject parent, final String childName) {
        final JsonValue child = parent.get(childName);
        return Optional.of(child)
                .filter(c -> JsonValue.ValueType.OBJECT.equals(c.getValueType()))
                .map(JsonValue::asJsonObject)
                .map(c -> c.containsKey(ENCRYPTED))
                .orElse(Boolean.FALSE);
    }

    private Boolean isJsonValueIsAnObject(final JsonValue jsonValue) {
        return JsonValue.ValueType.OBJECT.equals(jsonValue.getValueType());
    }

    private Boolean isJsonValueIsAnArray(final JsonValue jsonValue) {
        return JsonValue.ValueType.ARRAY.equals(jsonValue.getValueType());
    }

}
