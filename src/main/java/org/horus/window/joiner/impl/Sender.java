package org.horus.window.joiner.impl;

import org.horus.utils.rejection.Rejection;
import org.horus.window.joiner.TimeWindowed;

public interface Sender<K> {

    void rejectRightSide(Rejection<TimeWindowed<K>> rightSide);

    void rejectLeftSide(Rejection<TimeWindowed<K>> leftSide);

    void bounceLeftSide(TimeWindowed<K> leftSide);

    void sendJoin(TimeWindowed<K> leftSide, TimeWindowed<K> rightSide);

}
