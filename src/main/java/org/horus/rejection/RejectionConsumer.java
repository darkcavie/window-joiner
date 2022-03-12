package org.horus.rejection;

import java.util.function.Consumer;

public interface RejectionConsumer<T> extends Consumer<Rejection<T>> {
}
