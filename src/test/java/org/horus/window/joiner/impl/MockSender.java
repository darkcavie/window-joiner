package org.horus.window.joiner.impl;

import org.horus.utils.rejection.Rejection;
import org.horus.window.joiner.TimeWindowed;

import static org.junit.jupiter.api.Assertions.fail;

class MockSender implements Sender<String> {

    private int counter;

    final void inc() {
        counter++;
    }

    final int count() {
        return counter;
    }

    @Override
    public void rejectRightSide(Rejection<TimeWindowed<String>> rightSide) {
        fail("It must not reject the right Side");
    }

    @Override
    public void rejectLeftSide(Rejection<TimeWindowed<String>> leftSide) {
        fail("It must not reject the left side");
    }

    @Override
    public void bounceLeftSide(TimeWindowed<String> leftSide) {
        fail("It must not bounce left side");
    }

    @Override
    public void sendJoin(TimeWindowed<String> leftSide, TimeWindowed<String> rightSide) {
        fail("It must not join");
    }

}
