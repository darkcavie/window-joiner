package org.horus.window.joiner;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public interface TimeWindowed<K> {

    K getKey();

    InputStream getPayLoad();

    long getTimestamp();

    default boolean unCastedEquals(Object other) {
        return Optional.ofNullable(other)
                .filter(TimeWindowed.class::isInstance)
                .map(TimeWindowed.class::cast)
                .filter(this::castedEquals)
                .isPresent();
    }

    private boolean castedEquals(TimeWindowed<?> object) {
        return Objects.equals(getKey(), object.getKey()) &&
                getTimestamp() == object.getTimestamp();
    }

}
