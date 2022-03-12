package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

import static org.junit.jupiter.api.Assertions.fail;

class AcceptMockStorage extends MockStorage {

    private TimeWindowed<String> stored;

    @Override
    public void add(TimeWindowed<String> timeWindowed) {
        if(stored != null) {
            fail("There is another value stored");
        }
        stored = timeWindowed;
    }

    TimeWindowed<String> getStored() {
        return stored;
    }

}
