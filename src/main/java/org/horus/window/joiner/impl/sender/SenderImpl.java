package org.horus.window.joiner.impl.sender;

import org.horus.utils.rejection.Rejection;
import org.horus.utils.rejection.RejectionConsumer;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.impl.Sender;
import org.horus.window.joiner.impl.TimeWindowedConsumer;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

public class SenderImpl<K> implements Sender<K> {

    private static final Logger LOG = getLogger(SenderImpl.class);

    private TimeWindowedBiConsumer<K> joinedConsumer;

    private RejectionConsumer<TimeWindowed<K>> leftRejectedConsumer;

    private RejectionConsumer<TimeWindowed<K>> rightRejectedConsumer;

    private TimeWindowedConsumer<K> bounceConsumer;

    public void setJoinedConsumer(TimeWindowedBiConsumer<K> joinedConsumer) {
        this.joinedConsumer = requireNonNull(joinedConsumer);
    }

    public void setLeftRejectedConsumer(RejectionConsumer<TimeWindowed<K>> leftRejectedConsumer) {
        this.leftRejectedConsumer = leftRejectedConsumer;
    }

    public void setRightRejectedConsumer(RejectionConsumer<TimeWindowed<K>> rightRejectedConsumer) {
        this.rightRejectedConsumer = rightRejectedConsumer;
    }

    public void setBounceConsumer(TimeWindowedConsumer<K> bounceConsumer) {
        this.bounceConsumer = requireNonNull(bounceConsumer);
    }

    public void postConstruct() {
        requireNonNull(joinedConsumer, "The joined consumer is mandatory");
        requireNonNull(bounceConsumer, "The bounce consumer is mandatory");
    }

    @Override
    public void rejectRightSide(Rejection<TimeWindowed<K>> rightSide) {
        if(rightRejectedConsumer != null) {
            rightRejectedConsumer.accept(rightSide);
        }
        LOG.info("There is no right rejection consumer. Rejection {}", rightSide);
    }

    @Override
    public void rejectLeftSide(Rejection<TimeWindowed<K>> leftSide) {
        if(leftRejectedConsumer != null) {
            leftRejectedConsumer.accept(leftSide);
        }
        LOG.info("There is no left rejection consumer. Rejection {}", leftSide);
    }

    @Override
    public void bounceLeftSide(TimeWindowed<K> leftSide) {
        requireNonNull(leftSide,"The bounced side is mandatory");
        LOG.debug("Bouncing key {}", leftSide.getKey());
        bounceConsumer.accept(leftSide);
    }

    @Override
    public void sendJoin(final TimeWindowed<K> leftSide, final TimeWindowed<K> rightSide) {
        final K key;
        final Supplier<K> orGet;

        orGet = () -> Optional.ofNullable(rightSide)
                .map(TimeWindowed::getKey)
                .orElse(null);
        key = Optional.ofNullable(leftSide)
                .map(TimeWindowed::getKey)
                .orElseGet(orGet);
        if(key == null) {
            LOG.warn("Received null in both sides");
            return;
        }
        LOG.info("Sending join with key {}", key);
        joinedConsumer.accept(leftSide, rightSide);
    }

}
