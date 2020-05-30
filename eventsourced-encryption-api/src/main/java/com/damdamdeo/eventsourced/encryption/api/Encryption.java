package com.damdamdeo.eventsourced.encryption.api;

public interface Encryption {

    String generateNewSecret();

    String encrypt(String strToEncrypt, String secret);

    String decrypt(String strToDecrypt, String secret);

}
