package org.horus.window.joiner.impl;

import org.horus.storage.StorageException;
import org.horus.window.joiner.TimeWindowed;

public interface WindowedStorage<K> {

    void add(TimeWindowed<K> timeWindowed) throws StorageException;

    void getByKey(K key, TimeWindowedConsumer<K> consumer) throws StorageException;

}
