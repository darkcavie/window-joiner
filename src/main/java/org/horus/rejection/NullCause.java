package org.horus.rejection;

class NullCause implements Cause {

    @Override
    public String getCause() {
        return "Null source";
    }

    @Override
    public String getFormattedMessage() {
        return "cause.null";
    }

    @Override
    public Object[] getMessageParameters() {
        return new Object[0];
    }

}
