package org.horus.window.joiner.sender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenderImplTest {

    private SenderImpl sender;

    @BeforeEach
    void setUp() {
        sender = new SenderImpl();
    }

    @Test
    void setJoinedConsumer() {
        assertThrows(NullPointerException.class, () -> sender.setJoinedConsumer(null));
    }

    @Test
    void setLeftRejectedConsumer() {
        assertDoesNotThrow(() -> sender.setLeftRejectedConsumer(null));
    }

    @Test
    void setRightRejectedConsumer() {
        assertDoesNotThrow(() -> sender.setRightRejectedConsumer(null));
    }

    @Test
    void setBounceConsumer() {
        assertThrows(NullPointerException.class, () -> sender.setBounceConsumer(null));
    }

    @Test
    void postConstruct() {
        assertThrows(NullPointerException.class, sender::postConstruct);
    }

    @Test
    void rejectRightSide() {
    }

    @Test
    void rejectLeftSide() {
    }

    @Test
    void bounceLeftSide() {
    }

    @Test
    void sendJoin() {
    }
}