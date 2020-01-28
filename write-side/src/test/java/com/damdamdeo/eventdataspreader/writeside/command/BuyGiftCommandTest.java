package com.damdamdeo.eventdataspreader.writeside.command;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class BuyGiftCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(BuyGiftCommand.class).verify();
    }

}
