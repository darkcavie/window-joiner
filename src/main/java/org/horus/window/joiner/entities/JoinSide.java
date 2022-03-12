package org.horus.window.joiner.entities;

import java.io.InputStream;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class JoinSide<K> {

    private K key;

    private Instant timestamp;

    private InputStream payLoad;

    public JoinSide() {
    }

    public boolean isInWindow(long period) {
        return Instant.now()
                .minusMillis(period)
                .isBefore(timestamp);
    }

    public K getKey() {
        return key;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public InputStream getPayLoad() {
        return payLoad;
    }

    public void setKey(K key) {
        this.key = requireNonNull(key);
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = requireNonNull(timestamp);
    }

    public void setPayLoad(InputStream payLoad) {
        this.payLoad = requireNonNull(payLoad);
    }

}
