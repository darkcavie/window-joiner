package org.horus.rejection;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class SimpleRejection<T> implements Rejection<T> {

    private final T rejected;

    private final Cause cause;

    SimpleRejection(T rejected, String cause, String formattedMessage) {
        this.rejected = requireNonNull(rejected, "The parameter rejected is mandatory");
        this.cause = new SimpleCause(cause, formattedMessage);
    }

    @Override
    public T getRejected() {
        return rejected;
    }

    @Override
    public Stream<Cause> causeStream() {
        return Stream.of(cause);
    }

}
