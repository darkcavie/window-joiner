package org.horus.window.joiner.impl;

import org.horus.rejection.Rejection;
import org.horus.window.joiner.JoinerUseCase;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.entities.JoinSide;
import org.horus.window.joiner.entities.LeftSide;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.Optional;

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
        this.sender = requireNonNull(sender, "The sender dependency is mandatoy");
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

    @Override
    public void receiveRightSide(TimeWindowed<K> rightSide) {
        LOG.debug("Receive right join started");
        tryBuildRightSide(rightSide).ifPresent(this::processReceivedRightSide);
    }

    Optional<JoinSide<K>> tryBuildRightSide(TimeWindowed<K> rightSide) {
        return new SideBuilder<K, JoinSide<K>, TimeWindowed<K>>()
                .addRejectionConsumer(sender::rejectRightSide)
                .loadSource(rightSide)
                .setConstructor(JoinSide::new)
                .build();
    }

    void processReceivedLeftSide(final LeftSide<K> leftSide) {
        final K key;
        final int founded;

        key = leftSide.getKey();
        LOG.debug("Received left side with key {}", key);
        if(leftSide.isInWindow(windowConf.getPeriod())) {
            founded = rightStorage.search(key, rightSide -> tryJoin(leftSide, rightSide));
            if(founded == 0) {
                sender.bounceLeftSide(adapt(leftSide));
                return;
            }
            if(founded == 1) {
                LOG.debug("Founded right join with key {}", key);
                return;
            }
            LOG.warn("Founded {} matches for the key {}", founded, key);
            return;
        }
        sender.rejectLeftSide(Rejection.singleCause(adapt(leftSide), "Out of Window",
                "left.window.out"));
    }

    void tryJoin(final LeftSide<K> leftSide, final TimeWindowed<K> rightTimeWindowed) {
        final TimeWindowed<K> adaptedLeftTime;
        final Rejection<TimeWindowed<K>> rejection;

        tryBuildRightSide(rightTimeWindowed).ifPresent(leftSide::setRightSide);
        adaptedLeftTime = adapt(leftSide);
        if(leftSide.isaMatchIn(windowConf.getPeriod())) {
            sender.sendJoin(adaptedLeftTime, rightTimeWindowed);
            return;
        }
        rejection = Rejection.singleCause(adaptedLeftTime, "Not match", "left.match.not");
        sender.rejectLeftSide(rejection);
    }

    void processReceivedRightSide(JoinSide<K> rightSide) {
        final TimeWindowed<K> adaptedSide;
        final Rejection<TimeWindowed<K>> rejection;

        adaptedSide = adapt(rightSide);
        if(rightSide.isInWindow(windowConf.getPeriod())) {
            rightStorage.add(adaptedSide);
            return;
        }
        rejection = Rejection.singleCause(adaptedSide, "Out of Window", "right.window.out");
        sender.rejectRightSide(rejection);
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

}
