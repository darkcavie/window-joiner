package org.horus.rejection;

import java.util.Optional;

public interface Cause {

    static Cause singleCause(String cause, String formattedMessage) {
        return singleCause(cause, formattedMessage);
    }

    String getCause();

    String getFormattedMessage();

    Object[] getMessageParameters();

    default Optional<Throwable> optThrowable() {
        return Optional.empty();
    }

}
