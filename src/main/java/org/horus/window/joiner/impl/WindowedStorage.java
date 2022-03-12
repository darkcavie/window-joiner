package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

public interface WindowedStorage<K> {

    void add(TimeWindowed<K> timeWindowed);

    int search(K key, TimeWindowedConsumer<K> consumer);

}
