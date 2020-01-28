package com.damdamdeo.eventdataspreader.writeside.command;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DebitAccountCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebitAccountCommand.class).verify();
    }

}
