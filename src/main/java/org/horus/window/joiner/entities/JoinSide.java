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
        return new ByteArrayInputStream(payLoad);
    }

    public void setKey(K key) {
        this.key = requireNonNull(key);
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = requireNonNull(timestamp);
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
