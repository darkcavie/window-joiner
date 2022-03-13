package org.horus.rejection;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RejectionTest {

    private Rejection<?> rejection;

    @Test
    void nullRejection() {
        final Optional<Cause> optCause;
        final Cause cause;

        rejection = Rejection.nullRejection();
        assertNotNull(rejection);
        assertNull(rejection.getRejected());
        optCause = rejection.causeStream().findFirst();
        assertTrue(optCause.isPresent());
        cause = optCause.get();
        assertEquals("Null source", cause.getCause());
        assertEquals("cause.null", cause.getFormattedMessage());
        assertEquals(0, cause.getMessageParameters().length);
    }

    @Test
    void singleCause() {
        final Optional<Cause> optCause;
        final Cause cause;

        rejection = Rejection.singleCause("Rejected string", "Rejection cause", "rejection");
        assertEquals("Rejected string", rejection.getRejected());
        optCause = rejection.causeStream().findFirst();
        assertTrue(optCause.isPresent());
        cause = optCause.get();
        assertEquals("Rejection cause", cause.getCause());
        assertEquals("rejection", cause.getFormattedMessage());
        assertTrue(cause.optThrowable().isEmpty());
        assertEquals(0, cause.getMessageParameters().length);
    }

}
