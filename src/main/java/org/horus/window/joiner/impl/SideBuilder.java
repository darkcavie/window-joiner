package org.horus.window.joiner.impl;

import org.horus.entitybuilder.EntityBuilder;
import org.horus.rejection.Rejection;
import org.horus.window.joiner.TimeWindowed;
import org.horus.window.joiner.entities.JoinSide;

class SideBuilder<K, E extends JoinSide<K>, T extends TimeWindowed<K>> extends EntityBuilder<E, T> {

    @Override
    protected void assemble(E joinSide, T timeWindowed) {
        if(timeWindowed == null) {
            sendRejection(Rejection.nullRejection());
            return;
        }
        put("key", timeWindowed.getKey(), joinSide::setKey);
        putInstantFromMillis("timestamp", timeWindowed.getTimestamp(), joinSide::setTimestamp);
        joinSide.setPayLoad(timeWindowed.getPayLoad());
    }

}
