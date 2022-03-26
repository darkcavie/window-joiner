package org.horus.window.joiner.impl.storage;

import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.impl.TimeWindowedConsumer;
import org.horus.window.joiner.impl.WindowedStorage;

public class WindowedStorageImpl<K> implements WindowedStorage<K> {

    @Override
    public void add(TimeWindowed<K> timeWindowed) {

    }

    @Override
    public void getByKey(K key, TimeWindowedConsumer<K> consumer) {
    }

}
