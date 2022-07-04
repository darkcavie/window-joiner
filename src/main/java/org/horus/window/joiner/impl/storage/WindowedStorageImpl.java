package org.horus.window.joiner.impl.storage;

import org.horus.storage.Storage;
import org.horus.storage.StorageException;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.impl.TimeWindowedConsumer;
import org.horus.window.joiner.impl.WindowConf;
import org.horus.window.joiner.impl.WindowedStorage;

import static java.util.Objects.requireNonNull;

public class WindowedStorageImpl<K> implements WindowedStorage<K> {

    private Storage<K, TimeWindowed<K>> storage;

    private WindowConf conf;

    public void setStorage(Storage<K, TimeWindowed<K>> storage) {
        this.storage = requireNonNull(storage, "Storage can not be null");
    }

    public void setConf(WindowConf conf) {
        this.conf = requireNonNull(conf, "Configuration can not be null");
    }

    public void checkPostBuild() {
        requireNonNull(storage, "Storage is Mandatory");
        requireNonNull(conf, "Configuration is mandatory");
    }

    @Override
    public void add(TimeWindowed<K> timeWindowed) throws StorageException {
        storage.upsert(timeWindowed.getKey(), timeWindowed);
    }

    @Override
    public void getByKey(K key, TimeWindowedConsumer<K> consumer) throws StorageException {
        storage.getByKey(key, consumer);
    }

}
