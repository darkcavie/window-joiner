package org.horus.window.joiner.impl;

import org.horus.utils.entitybuilder.EntityBuilder;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.entities.JoinSide;

/**
 * Join side builder
 * @param <K> The key class
 * @param <E> The Join Side entity with key of type K
 * @param <T> The Time windowed contract with  key of type K
 */
class SideBuilder<K, E extends JoinSide<K>, T extends TimeWindowed<K>> extends EntityBuilder<E, T> {

    @Override
    protected void assemble(E joinSide, T timeWindowed) {
        put("key", timeWindowed.getKey(), joinSide::setKey);
        putInstantFromMillis("timestamp", timeWindowed.getTimestamp(), joinSide::setTimestamp);
        put("payLoad", timeWindowed.getPayLoad(), joinSide::setPayLoad);
    }

}
