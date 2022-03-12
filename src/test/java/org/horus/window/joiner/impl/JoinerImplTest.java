package org.horus.window.joiner.impl;

import org.horus.rejection.Rejection;
import org.horus.window.joiner.TimeWindowed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

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

    private void polish() {
        polish(0, new MockStorage());
    }

    private void polish(final long windowTime, final MockStorage mockStorage) {
        joiner.setWindowConf(() -> windowTime);
        joiner.setRightStorage(mockStorage);
        joiner.setSender(sender);
        joiner.postConstruct();
    }

    TimeWindowed<String> mockWindowed(final String key, final long time) {
        return new TimeWindowed<>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public InputStream getPayLoad() {
                return InputStream.nullInputStream();
            }

            @Override
            public long getTimestamp() {
                return time;
            }

            @Override
            public boolean equals(Object other) {
                return unCastedEquals(other);
            }

        };
    }

}
