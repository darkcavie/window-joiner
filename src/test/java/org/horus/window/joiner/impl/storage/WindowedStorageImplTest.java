package org.horus.window.joiner.impl.storage;

import org.horus.storage.StorageException;
import org.horus.storage.StorageTestTools;
import org.horus.window.joiner.TimeWindowed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class WindowedStorageImplTest {

    private WindowedStorageImpl<Integer> impl;

    @BeforeEach
    void setUp() {
        impl = new WindowedStorageImpl<>();
    }

    @Test
    void setStorage() {
        assertThrows(NullPointerException.class, () -> impl.setStorage(null));
    }

    @Test
    void setConf() {
        assertThrows(NullPointerException.class, () -> impl.setConf(null));
    }

    @Test
    void checkPostBuild() {
        impl.setConf(() -> 0L);
        impl.setStorage(StorageTestTools.voidStorage());
        assertDoesNotThrow(impl::checkPostBuild);
    }

    @Test
    void add() {
        impl.setConf(() -> 0L);
        impl.setStorage(StorageTestTools.voidStorage());
        assertDoesNotThrow(() -> impl.add(timeWindowed(1)));
    }

    private TimeWindowed<Integer> timeWindowed(final int key) {
        return new TimeWindowed<>() {

            @Override
            public Integer getKey() {
                return key;
            }

            @Override
            public InputStream getPayLoad() {
                return null;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }
        };
    }

    @Test
    void getByKey() throws StorageException {
        final TimeWindowed<Integer> side;
        final AtomicInteger counter;

        side = timeWindowed(1000);
        counter = new AtomicInteger(0);
        impl.setConf(() -> 0L);
        impl.setStorage(StorageTestTools.memoryStorage());
        impl.add(side);
        impl.getByKey(1000, t -> {
            assertEquals(1000, t.getKey());
            counter.incrementAndGet();
        });
        assertEquals(1, counter.get());
    }

}