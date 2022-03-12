package org.horus.window.joiner.entities;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

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

}