package org.horus.entitybuilder;

import org.horus.rejection.Cause;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

class ExceptionCause implements Cause {

    private final Throwable throwable;

    private final Object value;

    private final String formattedMessage;

    ExceptionCause(String fieldName, Object value, Throwable throwable) {
        requireNonNull(fieldName, "Field name parameter is mandatory");
        this.throwable = requireNonNull(throwable, "Throwable parameter is mandatory");
        this.value = requireNonNull(value, "Value parameter is mandatory");
        formattedMessage = String.format("%s.%s", fieldName, throwable.getClass().getName());
    }

    @Override
    public String getCause() {
        return throwable.getMessage();
    }

    @Override
    public String getFormattedMessage() {
        return formattedMessage;
    }

    @Override
    public Object[] getMessageParameters() {
        return new Object[]{value};
    }

    @Override
    public Optional<Throwable> optThrowable() {
        return Optional.of(throwable);
    }

}
