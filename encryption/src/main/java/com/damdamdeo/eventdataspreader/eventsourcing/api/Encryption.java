package com.damdamdeo.eventdataspreader.eventsourcing.api;

public interface Encryption {

    String generateNewSecret();

    String encrypt(String strToEncrypt, String secret);

    String decrypt(String strToDecrypt, String secret);

}
