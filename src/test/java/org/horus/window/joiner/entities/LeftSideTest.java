package org.horus.window.joiner.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeftSideTest {

    private LeftSide<Integer> leftSide;

    @BeforeEach
    void setUp() {
        leftSide = new LeftSide<>();
    }

    @Test
    void setRightSide() {
        assertDoesNotThrow(() -> leftSide.setRightSide(null));
    }

    @Test
    void isaMatchIn() {
        assertFalse(leftSide.isaMatchIn(0));
    }

    @Test
    void isaMatchInNegativePeriodFails() {
        assertThrows(IllegalArgumentException.class, () -> leftSide.isaMatchIn(-1));
    }

}