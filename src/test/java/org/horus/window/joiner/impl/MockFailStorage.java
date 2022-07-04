package org.horus.window.joiner.impl;

import org.horus.storage.StorageException;
import org.horus.window.joiner.TimeWindowed;

class MockFailStorage implements WindowedStorage<String> {

    @Override
    public void add(TimeWindowed<String> timeWindowed) throws StorageException {
        throw new StorageException("Mocking an add failure");
    }

    @Override
    public void getByKey(String key, TimeWindowedConsumer<String> consumer) throws StorageException {
        throw new StorageException("Mocking a get by key failure");
    }

}
