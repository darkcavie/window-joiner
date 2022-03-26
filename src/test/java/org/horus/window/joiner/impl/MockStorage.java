package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

class MockStorage implements WindowedStorage<String> {

    @Override
    public void add(TimeWindowed<String> timeWindowed) {
    }

    @Override
    public void search(String key, TimeWindowedConsumer<String> consumer) {
    }

}
