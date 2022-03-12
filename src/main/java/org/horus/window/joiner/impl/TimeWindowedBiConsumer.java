package org.horus.window.joiner.impl;

import org.horus.window.joiner.TimeWindowed;

import java.util.function.BiConsumer;

public interface TimeWindowedBiConsumer<K> extends BiConsumer<TimeWindowed<K>, TimeWindowed> {
}
