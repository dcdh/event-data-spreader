package com.damdamdeo.eventsourced.encryption.infra.jsonb;

import com.damdamdeo.eventsourced.encryption.api.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonValue;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class JsonbCryptoDecryptServiceTest {

    @Inject
    JsonbCryptoService jsonbCryptoService;

    @InjectMock
    SecretStore secretStore;

    @InjectMock
    @AESEncryptionQualifier
    Encryption encryption;

    @Test
    public void should_not_decrypt_when_encrypted_is_not_present() {
        // Given
        final JsonValue givenJsonValue = Json.createReader(new StringReader("{\"message\":\"hello!\"}")).readObject();

        // When
        final JsonValue jsonValueDecrypted = jsonbCryptoService.decrypt(givenJsonValue);

        // Then
        assertEquals(Json.createObjectBuilder(Collections.singletonMap("message", "hello!")).build(), jsonValueDecrypted);
        verify(secretStore, times(0)).read(any());
    }

    @Test
    public void should_not_decrypt_when_json_is_not_an_object() {
        // Given
        final JsonValue givenJsonValue = Json.createValue(0);

        // When
        final JsonValue jsonValueDecrypted = jsonbCryptoService.decrypt(givenJsonValue);

        // Then
        assertEquals(Json.createValue(0), jsonValueDecrypted);
        verify(secretStore, times(0)).read(any());
    }

    @Test
    public void should_decrypt_string() {
        // Given
        final JsonValue givenJsonValue = Json.createReader(new StringReader("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"string\"}")).readObject();
        final Secret secret = mock(Secret.class);
        final AggregateRootId aggregateRootId = new JsonObjectEncryptedAggregateRootId("aggregateRootType", "aggregateRootId");
        doReturn("decrypted").when(secret).decrypt(eq(aggregateRootId), eq("encrypted"), any(Encryption.class));
        doReturn(secret).when(secretStore).read(aggregateRootId);

        // When
        final JsonValue jsonValueDecrypted = jsonbCryptoService.decrypt(givenJsonValue);

        // Then
        assertEquals(Json.createValue("decrypted"), jsonValueDecrypted);

        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_integer() {
        // Given
        final JsonValue givenJsonValue = Json.createReader(new StringReader("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"integer\"}")).readObject();
        final Secret secret = mock(Secret.class);
        final AggregateRootId aggregateRootId = new JsonObjectEncryptedAggregateRootId("aggregateRootType", "aggregateRootId");
        doReturn("0").when(secret).decrypt(eq(aggregateRootId), eq("encrypted"), any(Encryption.class));
        doReturn(secret).when(secretStore).read(aggregateRootId);

        // When
        final JsonValue jsonValueDecrypted = jsonbCryptoService.decrypt(givenJsonValue);

        // Then
        assertEquals(Json.createValue(0), jsonValueDecrypted);

        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_long() {
        // Given
        final JsonValue givenJsonValue = Json.createReader(new StringReader("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"long\"}")).readObject();
        final Secret secret = mock(Secret.class);
        final AggregateRootId aggregateRootId = new JsonObjectEncryptedAggregateRootId("aggregateRootType", "aggregateRootId");
        doReturn("0").when(secret).decrypt(eq(aggregateRootId), eq("encrypted"), any(Encryption.class));
        doReturn(secret).when(secretStore).read(aggregateRootId);

        // When
        final JsonValue jsonValueDecrypted = jsonbCryptoService.decrypt(givenJsonValue);

        // Then
        assertEquals(Json.createValue(0l), jsonValueDecrypted);

        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_big_decimal() {
        // Given
        final JsonValue givenJsonValue = Json.createReader(new StringReader("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"bigDecimal\"}")).readObject();
        final Secret secret = mock(Secret.class);
        final AggregateRootId aggregateRootId = new JsonObjectEncryptedAggregateRootId("aggregateRootType", "aggregateRootId");
        doReturn("0").when(secret).decrypt(eq(aggregateRootId), eq("encrypted"), any(Encryption.class));
        doReturn(secret).when(secretStore).read(aggregateRootId);

        // When
        final JsonValue jsonValueDecrypted = jsonbCryptoService.decrypt(givenJsonValue);

        // Then
        assertEquals(Json.createValue(BigDecimal.ZERO), jsonValueDecrypted);

        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_big_integer() {
        // Given
        final JsonValue givenJsonValue = Json.createReader(new StringReader("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"bigInteger\"}")).readObject();
        final Secret secret = mock(Secret.class);
        final AggregateRootId aggregateRootId = new JsonObjectEncryptedAggregateRootId("aggregateRootType", "aggregateRootId");
        doReturn("0").when(secret).decrypt(eq(aggregateRootId), eq("encrypted"), any(Encryption.class));
        doReturn(secret).when(secretStore).read(aggregateRootId);

        // When
        final JsonValue jsonValueDecrypted = jsonbCryptoService.decrypt(givenJsonValue);

        // Then
        assertEquals(Json.createValue(BigInteger.ZERO), jsonValueDecrypted);

        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_recursively() throws Exception {
        // Given
        final String givenJsonEncrypted = new String(Files.readAllBytes(Paths.get(getClass().getResource("/jsonEncrypted.json").toURI())));
        final JsonValue givenJsonValue = Json.createReader(new StringReader(givenJsonEncrypted)).readValue();

        // define CarAggregateRoot Car01 decrypt behavior
        final Secret carAggregateRootCar01Secret = mock(Secret.class, RETURNS_DEEP_STUBS);
        doReturn("Damien").when(carAggregateRootCar01Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("CarAggregateRoot", "Car01")),
                eq("ownerAsEncryptedValue"),
                any());
        doReturn(carAggregateRootCar01Secret).when(secretStore).read(new JsonObjectEncryptedAggregateRootId("CarAggregateRoot", "Car01"));

        // define DriverAggregateRoot Driver00 decrypt behavior
        final Secret driverAggregateRootDriver00Secret = mock(Secret.class, RETURNS_DEEP_STUBS);
        doReturn("Damien").when(driverAggregateRootDriver00Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver00")),
                eq("driver[0]NameAsEncryptedValue"),
                any());
        doReturn("37").when(driverAggregateRootDriver00Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver00")),
                eq("driver[0]AgeAsEncryptedValue"),
                any());
        doReturn("2000").when(driverAggregateRootDriver00Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver00")),
                eq("driver[0]LicenseYearOfLicenceAsEncryptedValue"),
                any());
        doReturn("C").when(driverAggregateRootDriver00Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver00")),
                eq("driver[0]LicenseCategoryAsEncryptedValue"),
                any());
        doReturn(driverAggregateRootDriver00Secret).when(secretStore).read(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver00"));
        // define DriverAggregateRoot Driver01 decrypt behavior
        final Secret driverAggregateRootDriver01Secret = mock(Secret.class, RETURNS_DEEP_STUBS);
        doReturn("Roger").when(driverAggregateRootDriver01Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[1]NameAsEncryptedValue"),
                any());
        doReturn("30").when(driverAggregateRootDriver01Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[1]AgeAsEncryptedValue"),
                any());
        doReturn("1980").when(driverAggregateRootDriver01Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[1]LicenseYearOfLicenceAsEncryptedValue"),
                any());
        doReturn("B").when(driverAggregateRootDriver01Secret).decrypt(eq(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[1]LicenseCategoryAsEncryptedValue"),
                any());
        doReturn(driverAggregateRootDriver01Secret).when(secretStore).read(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver01"));

        // When
        final JsonValue jsonValue = jsonbCryptoService.recursiveDecrypt(givenJsonValue);

        // Then
        final String expectedJsonDecrypted = new String(Files.readAllBytes(Paths.get(getClass().getResource("/jsonDecrypted.json").toURI())));
        System.out.println(jsonValue.toString());
        JSONAssert.assertEquals(expectedJsonDecrypted, jsonValue.toString(), true);

        verify(carAggregateRootCar01Secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, atLeastOnce()).read(new JsonObjectEncryptedAggregateRootId("CarAggregateRoot", "Car01"));

        verify(driverAggregateRootDriver00Secret, atLeastOnce()).decrypt(any(), any(), any());
        verify(secretStore, atLeastOnce()).read(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver00"));
        verify(driverAggregateRootDriver01Secret, atLeastOnce()).decrypt(any(), any(), any());
        verify(secretStore, atLeastOnce()).read(new JsonObjectEncryptedAggregateRootId("DriverAggregateRoot", "Driver01"));
    }

}
