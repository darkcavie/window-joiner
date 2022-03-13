package org.horus.window.joiner.sender;

import org.horus.rejection.Rejection;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.TimeWindowedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SenderImplTest {

    private SenderImpl<String> sender;

    @BeforeEach
    void setUp() {
        sender = new SenderImpl<>();
    }

    @Test
    void setJoinedConsumerNullFails() {
        assertThrows(NullPointerException.class, () -> sender.setJoinedConsumer(null));
    }

    @Test
    void setJoinedConsumer() {
        assertDoesNotThrow(() -> sender.setJoinedConsumer((t, u) -> {}));
    }

    @Test
    void setLeftRejectedConsumerNullIsFine() {
        assertDoesNotThrow(() -> sender.setLeftRejectedConsumer(null));
    }

    @Test
    void setLeftRejectedConsumer() {
        assertDoesNotThrow(() -> sender.setLeftRejectedConsumer(t -> {}));
    }

    @Test
    void setRightRejectedConsumer() {
        assertDoesNotThrow(() -> sender.setRightRejectedConsumer(null));
    }

    @Test
    void setBounceConsumerNullFails() {
        assertThrows(NullPointerException.class, () -> sender.setBounceConsumer(null));
    }

    @Test
    void setBounceConsumer() {
        assertDoesNotThrow(() -> sender.setBounceConsumer(t -> {}));
    }

    @Test
    void postConstructNullFails() {
        assertThrows(NullPointerException.class, sender::postConstruct);
    }

    @Test
    void postConstruct() {
        sender.setBounceConsumer(t -> {});
        sender.setJoinedConsumer((l,r) -> {});
        assertDoesNotThrow(sender::postConstruct);
    }

    @Test
    void rejectRightSideThereIsNoConsumer() {
        final TimeWindowed<String> timeWindowed;
        final Rejection<TimeWindowed<String>> rejection;

        timeWindowed = TimeWindowedTest.mockWindowed("mockKey", System.currentTimeMillis());
        rejection = Rejection.singleCause(timeWindowed, "none", "none");
        assertDoesNotThrow(() -> sender.rejectRightSide(rejection));
    }

    @Test
    void rejectRightSide() {
        final TimeWindowed<String> timeWindowed;
        final Rejection<TimeWindowed<String>> rejection;
        final AtomicInteger counter;

        timeWindowed = TimeWindowedTest.mockWindowed("mockKey", System.currentTimeMillis());
        rejection = Rejection.singleCause(timeWindowed, "none", "none");
        counter = new AtomicInteger();
        sender.setRightRejectedConsumer(right -> {
            counter.incrementAndGet();
            assertNotNull(right);
            assertEquals(right.getRejected(), timeWindowed);
        });
        sender.rejectRightSide(rejection);
        assertEquals(1, counter.get());
    }

    @Test
    void rejectLeftSideThereIsNoConsumer() {
        final TimeWindowed<String> timeWindowed;
        final Rejection<TimeWindowed<String>> rejection;

        timeWindowed = TimeWindowedTest.mockWindowed("mockKey", System.currentTimeMillis());
        rejection = Rejection.singleCause(timeWindowed, "none", "none");
        assertDoesNotThrow(() -> sender.rejectLeftSide(rejection));
    }

    @Test
    void rejectLeftSide() {
        final TimeWindowed<String> timeWindowed;
        final Rejection<TimeWindowed<String>> rejection;
        final AtomicInteger counter;

        timeWindowed = TimeWindowedTest.mockWindowed("mockKey", System.currentTimeMillis());
        rejection = Rejection.singleCause(timeWindowed, "none", "none");
        counter = new AtomicInteger();
        sender.setLeftRejectedConsumer(left -> {
            counter.incrementAndGet();
            assertNotNull(left);
            assertEquals(left.getRejected(), timeWindowed);
        });
        sender.rejectLeftSide(rejection);
        assertEquals(1, counter.get());
    }

    @Test
    void bounceLeftSide() {
    }

    @Test
    void sendJoin() {
    }
}