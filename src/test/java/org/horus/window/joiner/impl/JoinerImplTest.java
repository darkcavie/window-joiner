package org.horus.window.joiner.impl;

import org.horus.rejection.Cause;
import org.horus.rejection.Rejection;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.entities.JoinSide;
import org.horus.window.joiner.entities.LeftSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.horus.window.joiner.TimeWindowedTest.mockWindowed;
import static org.junit.jupiter.api.Assertions.*;

class JoinerImplTest {

    private JoinerImpl<String> joiner;

    private MockSender sender;

    @BeforeEach
    void setUp() {
        joiner = new JoinerImpl<>();
        sender = null;
    }

    @Test
    void setWindowConf() {
        assertThrows(NullPointerException.class, () -> joiner.setWindowConf(null));
    }

    @Test
    void setRightStorage() {
        assertThrows(NullPointerException.class, () -> joiner.setRightStorage(null));
    }

    @Test
    void setSender() {
        assertThrows(NullPointerException.class, () -> joiner.setSender(null));
    }

    @Test
    void postConstruct() {
        assertThrows(NullPointerException.class, joiner::postConstruct);
    }

    @Test
    void postConstructJustDependencies() {
        joiner.setRightStorage(new MockStorage());
        joiner.setWindowConf(() -> 0L);
        joiner.setSender(new MockSender());
        assertDoesNotThrow(joiner::postConstruct);
    }

    @Test
    void receiveLeftJoinRejected() {
        final TimeWindowed<String> original;

        original = mockWindowed("mockKey", 0);
        sender = new MockSender() {
            @Override
            public void rejectLeftSide(Rejection<TimeWindowed<String>> leftSide) {
                inc();
                assertEquals(original, leftSide.getRejected());
            }
        };
        polish();
        joiner.receiveLeftSide(original);
        assertEquals(1, sender.count());
    }

    @Test
    void receiveRightJoinRejected() {
        final TimeWindowed<String> original;

        original = mockWindowed("mockKey", 0);
        sender = new MockSender(){
            @Override
            public void rejectRightSide(Rejection<TimeWindowed<String>> rightSide) {
                inc();
                assertEquals(original, rightSide.getRejected());
            }
        };
        polish(0, new MockStorage());
        joiner.receiveRightSide(original);
        assertEquals(1, sender.count());
    }

    @Test
    void receiveLeftJoinBounced() {
        final TimeWindowed<String> original;

        original = mockWindowed("bounceKey", System.currentTimeMillis());
        sender = new MockSender() {
            @Override
            public void bounceLeftSide(TimeWindowed<String> bounced) {
                inc();
                assertEquals(original, bounced);
            }
        };
        polish(10_000L, new MockStorage()); //10 seconds
        joiner.receiveLeftSide(original);
        assertEquals(1, sender.count());
    }

    @Test
    void receiveLeftJoined() {
        final TimeWindowed<String> original;

        original = mockWindowed("joinKey", System.currentTimeMillis());
        sender = new MockSender() {
            @Override
            public void sendJoin(TimeWindowed<String> leftSide, TimeWindowed<String> rightSide) {
                inc();
                assertEquals(leftSide, original);
                assertNotNull(rightSide);
                assertEquals(leftSide.getKey(), rightSide.getKey());
            }
        };
        polish(10_000L, new SingleMockStorage()); //10 seconds
        joiner.receiveLeftSide(original);
        assertEquals(1, sender.count());
    }

    @Test
    void receiveRightStored() {
        final TimeWindowed<String> right, stored;
        final AcceptMockStorage storage;

        storage = new AcceptMockStorage();
        right = mockWindowed("storageKey", System.currentTimeMillis());
        sender = new MockSender();
        polish(10_000L, storage);
        joiner.receiveRightSide(right);
        stored = storage.getStored();
        assertNotNull(stored);
        assertEquals(stored, right);
    }

    @Test
    void receiveLeftJoinRejectedOutWindow() {
        final TimeWindowed<String> left;

        left = mockWindowed("joinKey", System.currentTimeMillis());
        sender = new MockSender() {
            @Override
            public void rejectLeftSide(Rejection<TimeWindowed<String>> rejection) {
                inc();
                assertEquals(left, rejection.getRejected());
            }
        };
        polish(5_000L, new OldMockStorage());
        joiner.receiveLeftSide(left);
        assertEquals(1, sender.count());
    }

    @Test
    void processReceivedLeftSide() {
        final LeftSide<String> original;

        original = new LeftSide<>();
        original.setKey("mockKey");
        original.setTimestamp(Instant.now());
        original.setPayLoad(new ByteArrayInputStream(new byte[0]));
        sender = new MockSender() {
            @Override
            public void sendJoin(TimeWindowed<String> leftSide, TimeWindowed<String> rightSide) {
                inc();
                assertEquals(original.getKey(), leftSide.getKey());
                assertEquals(original.getTimestamp().toEpochMilli(), leftSide.getTimestamp());
                assertNotNull(leftSide.getPayLoad());
            }
        };
        polish(10_000L, new DuplicateMockStorage());
        joiner.processReceivedLeftSide(original);
        assertEquals(2, sender.count());
    }

    private void polish() {
        polish(0, new MockStorage());
    }

    private void polish(final long windowTime, final MockStorage mockStorage) {
        joiner.setWindowConf(() -> windowTime);
        joiner.setRightStorage(mockStorage);
        joiner.setSender(sender);
        joiner.postConstruct();
    }

    @Test
    void tryMatchRightSideCatched() {
        final LeftSide<String> leftSide;
        final AtomicInteger counter;

        leftSide = new LeftSide<>();
        leftSide.setKey("mockKey");
        counter = new AtomicInteger(0);
        joiner.setRightStorage(new MockFailStorage());
        joiner.setSender(new MockSender() {
            @Override
            public void rejectLeftSide(Rejection<TimeWindowed<String>> rejection) {
                final TimeWindowed<String> leftSide;

                counter.incrementAndGet();
                assertNotNull(rejection);
                leftSide = rejection.getRejected();
                assertNotNull(leftSide);
                assertEquals("mockKey", leftSide.getKey());
            }
        });
        joiner.tryMatchRightSide(leftSide);
        assertEquals(1, counter.get());
    }

    @Test
    void tryProcessReceivedRightSideCatched() {
        final JoinSide<String> rightSide;
        final AtomicInteger counter;

        rightSide = new JoinSide<>();
        rightSide.setKey("mockKey");
        rightSide.setTimestamp(Instant.now());
        counter = new AtomicInteger(0);
        joiner.setRightStorage(new MockFailStorage());
        joiner.setWindowConf(() -> 5000L);
        joiner.setSender(new MockSender() {
            @Override
            public void rejectRightSide(Rejection<TimeWindowed<String>> rejection) {
                final TimeWindowed<String> rightSide;
                final Cause cause;

                assertNotNull(rejection);
                cause = rejection.causeStream().findFirst().orElseThrow();
                assertEquals("right.window.error", cause.getFormattedMessage());
                rightSide = rejection.getRejected();
                assertNotNull(rightSide);
                assertEquals("mockKey", rightSide.getKey());
                counter.incrementAndGet();
            }
        });
        joiner.tryProcessReceivedRightSide(rightSide);
        assertEquals(1, counter.get());
    }

}
