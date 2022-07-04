package org.horus.window.joiner.impl;

import org.horus.rejection.Rejection;
import org.horus.storage.StorageException;
import org.horus.window.joiner.JoinerUseCase;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.entities.JoinSide;
import org.horus.window.joiner.entities.LeftSide;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

public class JoinerImpl<K> implements JoinerUseCase<K> {

    private static final Logger LOG = getLogger(JoinerImpl.class);

    private WindowConf windowConf;

    private WindowedStorage<K> rightStorage;

    private Sender<K> sender;

    public void setWindowConf(WindowConf windowConf) {
        this.windowConf = requireNonNull(windowConf);
    }

    public void setRightStorage(WindowedStorage<K> rightStorage) {
        this.rightStorage = requireNonNull(rightStorage);
    }

    public void setSender(Sender<K> sender) {
        this.sender = requireNonNull(sender, "The sender dependency is mandatory");
    }

    public void postConstruct() {
        requireNonNull(sender, "The sender is mandatory");
        requireNonNull(rightStorage, "The right storage is mandatory");
        requireNonNull(windowConf, "The window configuration is mandatory");
        LOG.info("Joiner implementation well initialized");
    }

    @Override
    public void receiveLeftSide(TimeWindowed<K> leftSide) {
        LOG.debug("Receive left join started");
        new SideBuilder<K, LeftSide<K>, TimeWindowed<K>>()
                .addRejectionConsumer(sender::rejectLeftSide)
                .loadSource(leftSide)
                .setConstructor(LeftSide::new)
                .build()
                .ifPresent(this::processReceivedLeftSide);
    }

    void processReceivedLeftSide(final LeftSide<K> leftSide) {
        LOG.debug("Received left side with key {}", leftSide.getKey());
        if(leftSide.isInWindow(windowConf.getPeriod())) {
            tryMatchRightSide(leftSide);
            return;
        }
        rejectLeftSide(leftSide, "Out of Window", "left.window.out");
    }

    void tryMatchRightSide(final LeftSide<K> leftSide) {
        try {
            matchRightSide(leftSide);
        } catch (StorageException e) {
            rejectLeftSide(leftSide, "Error searching right side", "left.window.right.error");
        }
    }

    void matchRightSide(final LeftSide<K> leftSide) throws StorageException {
        final AtomicInteger founded;
        final K key;

        key = leftSide.getKey();
        founded = new AtomicInteger();
        rightStorage.getByKey(key, rightSide -> {
            founded.incrementAndGet();
            tryJoin(leftSide, rightSide);
        });
        switch(founded.get()) {
            case 0:
                sender.bounceLeftSide(adapt(leftSide));
                break;
            case 1:
                LOG.debug("Founded right join with key {}", key);
                break;
            default:
                LOG.warn("Founded {} matches for the key {}", founded, key);
        }
    }

    void tryJoin(final LeftSide<K> leftSide, final TimeWindowed<K> rightTimeWindowed) {
        final TimeWindowed<K> adaptedLeftTime;

        tryBuildRightSide(rightTimeWindowed).ifPresent(leftSide::setRightSide);
        if(leftSide.isaMatchIn(windowConf.getPeriod())) {
            adaptedLeftTime = adapt(leftSide);
            sender.sendJoin(adaptedLeftTime, rightTimeWindowed);
            return;
        }
        rejectLeftSide(leftSide,"Not match", "left.match.not");
    }

    void rejectLeftSide(final LeftSide<K> leftSide, String cause, String formattedMessage) {
        final TimeWindowed<K> timeWindowed;
        final Rejection<TimeWindowed<K>> rejection;

        LOG.info("Rejecting left side with key [{}] because {}", leftSide.getKey(), cause);
        timeWindowed = adapt(leftSide);
        rejection = Rejection.singleCause(timeWindowed, cause, formattedMessage);
        sender.rejectLeftSide(rejection);
    }

    private TimeWindowed<K> adapt(JoinSide<K> leftSide) {
        return new TimeWindowed<>() {
            @Override
            public K getKey() {
                return leftSide.getKey();
            }

            @Override
            public InputStream getPayLoad() {
                return leftSide.getPayLoad();
            }

            @Override
            public long getTimestamp() {
                return leftSide.getTimestamp().toEpochMilli();
            }

            @Override
            public boolean equals(Object object) {
                return unCastedEquals(object);
            }
        };
    }

    @Override
    public void receiveRightSide(TimeWindowed<K> rightSide) {
        LOG.debug("Receive right join started");
        tryBuildRightSide(rightSide).ifPresent(this::tryProcessReceivedRightSide);
    }

    Optional<JoinSide<K>> tryBuildRightSide(TimeWindowed<K> rightSide) {
        return new SideBuilder<K, JoinSide<K>, TimeWindowed<K>>()
                .addRejectionConsumer(sender::rejectRightSide)
                .loadSource(rightSide)
                .setConstructor(JoinSide::new)
                .build();
    }

    void tryProcessReceivedRightSide(JoinSide<K> rightSide) {
        try {
            processReceivedRightSide(rightSide);
        } catch (StorageException e) {
            rejectRightSide(rightSide, "Error adding rightSide", "right.window.error");
        }
    }

    void processReceivedRightSide(JoinSide<K> rightSide) throws StorageException {
        final TimeWindowed<K> adaptedSide;

        if(rightSide.isInWindow(windowConf.getPeriod())) {
            adaptedSide = adapt(rightSide);
            rightStorage.add(adaptedSide);
            return;
        }
        rejectRightSide(rightSide, "Out of Window", "right.window.out");
    }

    void rejectRightSide(JoinSide<K> rightSide, String cause, String formattedMessage) {
        final TimeWindowed<K> adaptedSide;
        final Rejection<TimeWindowed<K>> rejection;

        adaptedSide = adapt(rightSide);
        rejection = Rejection.singleCause(adaptedSide, cause, formattedMessage);
        sender.rejectRightSide(rejection);
    }

}
