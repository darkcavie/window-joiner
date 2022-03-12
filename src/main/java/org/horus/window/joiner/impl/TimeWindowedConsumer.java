package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

import java.util.function.Consumer;

public interface TimeWindowedConsumer<K> extends Consumer<TimeWindowed<K>> {
}
