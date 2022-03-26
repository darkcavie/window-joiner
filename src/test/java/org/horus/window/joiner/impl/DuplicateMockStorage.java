package org.horus.window.joiner.impl;

class DuplicateMockStorage extends SingleMockStorage{

    @Override
    public void search(String key, TimeWindowedConsumer<String> consumer) {
        consumer.accept(buildWithKey(key));
        consumer.accept(buildWithKey(key));
    }

}
