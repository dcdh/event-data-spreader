package com.damdamdeo.eventsourced.encryption.api;

public interface Secret {

    String SECRET_KEY = "Secret";
    String ENCRYPTION_STRATEGY = "encryptionStrategy";

    String secret();

    String encrypt(String strToEncrypt, Encryption encryption);

    String decrypt(String strToDecrypt, Encryption encryption);

}
