package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

public interface WindowedStorage<K> {

    void add(TimeWindowed<K> timeWindowed);

    void getByKey(K key, TimeWindowedConsumer<K> consumer);

}
