package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

import java.io.InputStream;

class SingleMockStorage extends MockStorage {

    @Override
    public int search(String key, TimeWindowedConsumer<String> consumer) {
        consumer.accept(buildWithKey(key));
        return 1;
    }

    TimeWindowed<String> buildWithKey(final String key) {
        return new TimeWindowed<String>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public InputStream getPayLoad() {
                return InputStream.nullInputStream();
            }

            @Override
            public long getTimestamp() {
                return System.currentTimeMillis();
            }
        };
    }

}
