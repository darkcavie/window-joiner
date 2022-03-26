package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

import java.io.InputStream;

class OldMockStorage extends MockStorage {

    @Override
    public void getByKey(String key, TimeWindowedConsumer<String> consumer) {
        consumer.accept(buildWithKey(key));
    }

    TimeWindowed<String> buildWithKey(final String key) {
        return new TimeWindowed<>() {
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
                return System.currentTimeMillis() - 10_000L;
            }
        };
    }


}
