package org.horus.window.joiner.sender;

import org.horus.rejection.Rejection;
import org.horus.rejection.RejectionConsumer;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.impl.Sender;
import org.horus.window.joiner.impl.TimeWindowedBiConsumer;
import org.horus.window.joiner.impl.TimeWindowedConsumer;

import static java.util.Objects.requireNonNull;

public class SenderImpl<K> implements Sender<K> {

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
    }

    @Override
    public void rejectLeftSide(Rejection<TimeWindowed<K>> leftSide) {
        if(leftRejectedConsumer != null) {
            leftRejectedConsumer.accept(leftSide);
        }
        //TODO: add logs
    }

    @Override
    public void bounceLeftSide(TimeWindowed<K> leftSide) {
        bounceConsumer.accept(leftSide);
    }

    @Override
    public void sendJoin(TimeWindowed<K> leftSide, TimeWindowed<K> rightSide) {
        joinedConsumer.accept(leftSide, rightSide);
    }

}
