package org.horus.entitybuilder;

import org.horus.rejection.Cause;
import org.horus.rejection.Rejection;

import java.util.List;
import java.util.stream.Stream;

class RejectionImpl<T> implements Rejection<T> {

    private final T rejected;

    private final List<Cause> causes;

    RejectionImpl(T rejected, List<Cause> causes) {
        this.rejected = rejected;
        this.causes = causes;
    }

    @Override
    public T getRejected() {
        return rejected;
    }

    @Override
    public Stream<Cause> causeStream() {
        return causes.stream();
    }

}
