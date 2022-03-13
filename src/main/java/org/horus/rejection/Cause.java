package org.horus.rejection;

import java.util.Optional;

public interface Cause {

    String getCause();

    String getFormattedMessage();

    Object[] getMessageParameters();

    default Optional<Throwable> optThrowable() {
        return Optional.empty();
    }

}
