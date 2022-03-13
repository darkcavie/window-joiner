package org.horus.window.joiner;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TimeWindowedTest {

    @Test
    void castedEqualsDifferentKey() {
        final TimeWindowed<String> one, another;
        final long now;

        now = System.currentTimeMillis();
        one = mockWindowed("one", now);
        another = mockWindowed("another", now);
        assertNotEquals(one, another);
    }

    @Test
    void castedEqualsDifferentTime() {
        final TimeWindowed<String> one, another;
        final long now;

        now = System.currentTimeMillis();
        one = mockWindowed("one", now);
        another = mockWindowed("one", now + 10_000L);
        assertNotEquals(one, another);
    }

    public static TimeWindowed<String> mockWindowed(final String key, final long time) {
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
