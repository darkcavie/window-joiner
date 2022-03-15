package org.horus.window.joiner.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JoinSideTest {

    private JoinSide<String> joinSide;

    @BeforeEach
    void setup() {
        joinSide = new JoinSide<>();
    }

    @Test
    void isInWindow() {
        final Instant now, tenSecondsAgo;

        now = Instant.now();
        tenSecondsAgo = now.minusSeconds(10);
        joinSide.setKey("mockKey");
        joinSide.setTimestamp(tenSecondsAgo);
        assertTrue(joinSide.isInWindow(20000));
    }

    @Test
    void notIsInWindow() {
        final Instant now, tenSecondsAgo;

        now = Instant.now();
        tenSecondsAgo = now.minusSeconds(10);
        joinSide.setKey("mockKey");
        joinSide.setTimestamp(tenSecondsAgo);
        assertFalse(joinSide.isInWindow(5000));
    }

    @Test
    void setPayLoadException() {
        final InputStream is;

        is = new InputStream(){
            @Override
            public int read() throws IOException {
                throw new IOException("Mock exception");
            }
        };
        assertThrows(IllegalArgumentException.class, () -> joinSide.setPayLoad(is));
    }

    @Test
    void isInWindowNoTimestampFail() {
        assertThrows(IllegalStateException.class, () -> joinSide.isInWindow(0L));
    }

    @Test
    void isInWindowNegativePeriodFail() {
        joinSide.setTimestamp(Instant.now());
        assertThrows(IllegalArgumentException.class, () -> joinSide.isInWindow(-1L));
    }

}