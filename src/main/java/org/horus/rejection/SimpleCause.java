package org.horus.rejection;

class SimpleCause implements Cause {

    private final String cause;

    private final String formattedMessage;

    SimpleCause(String cause, String formattedMessage) {
        this.cause = cause;
        this.formattedMessage = formattedMessage;
    }

    @Override
    public String getCause() {
        return cause;
    }

    @Override
    public String getFormattedMessage() {
        return formattedMessage;
    }

    @Override
    public Object[] getMessageParameters() {
        return new Object[0];
    }

}
