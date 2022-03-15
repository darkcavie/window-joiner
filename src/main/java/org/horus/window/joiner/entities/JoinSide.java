package org.horus.window.joiner.entities;

import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

public class JoinSide<K> {

    private static final Logger LOG = getLogger(JoinSide.class);

    private K key;

    private Instant timestamp;

    private byte[] payLoad;

    public boolean isInWindow(long period) {
        if(timestamp == null) {
            throw new IllegalStateException("The timestamp must exist before evaluation");
        }
        if(period < 0) {
            throw new IllegalArgumentException("The period must be positive");
        }
        return Instant.now()
                .minusMillis(period)
                .isBefore(timestamp);
    }

    public K getKey() {
        return requireNonNull(key);
    }

    public Instant getTimestamp() {
        return requireNonNull(timestamp);
    }

    public InputStream getPayLoad() {
        requireNonNull(payLoad, "Trying to get a payload without charge it before");
        return new ByteArrayInputStream(payLoad);
    }

    public void setKey(K key) {
        this.key = requireNonNull(key, "The key can not be null");
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = requireNonNull(timestamp, "The timestamp can not be null");
    }

    public void setPayLoad(InputStream payLoad) {
        try {
            this.payLoad = requireNonNull(payLoad).readAllBytes();
        } catch (IOException e) {
            LOG.error("Error trying get all bytes from incoming payload", e);
            throw new IllegalArgumentException("Not valid payload");
        }
    }

}
