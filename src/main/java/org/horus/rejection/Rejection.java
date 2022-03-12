package org.horus.rejection;

import java.util.stream.Stream;

public interface Rejection<T> {

    static <T> Rejection<T> nullRejection() {
        return new NullRejection<>();
    }

    static <T> Rejection<T> singleCause(T rejected, String cause, String formattedMessage) {
        return new SimpleRejection<>(rejected, cause, formattedMessage);
    }

    T getRejected();

    Stream<Cause> causeStream();

}
