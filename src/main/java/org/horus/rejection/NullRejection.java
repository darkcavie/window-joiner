package org.horus.rejection;

import java.util.stream.Stream;

class NullRejection<T> implements Rejection<T> {

    @Override
    public T getRejected() {
        return null;
    }

    @Override
    public Stream<Cause> causeStream() {
        return Stream.of(new NullCause());
    }

}
