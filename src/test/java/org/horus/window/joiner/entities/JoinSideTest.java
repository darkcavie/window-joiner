package org.horus.window.joiner.entities;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JoinSideTest {

    private JoinSide<String> joinSide;

    @Test
    void isInWindow() {
        final Instant now, tenSecondsAgo;

        now = Instant.now();
        tenSecondsAgo = now.minusSeconds(10);
        joinSide = new JoinSide<>();
        joinSide.setKey("mockKey");
        joinSide.setTimestamp(tenSecondsAgo);
        assertTrue(joinSide.isInWindow(20000));
    }

    @Test
    void notIsInWindow() {
        final Instant now, tenSecondsAgo;

        now = Instant.now();
        tenSecondsAgo = now.minusSeconds(10);
        joinSide = new JoinSide<>();
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
        joinSide = new JoinSide<>();
        assertThrows(IllegalArgumentException.class, () -> joinSide.setPayLoad(is));
    }

}