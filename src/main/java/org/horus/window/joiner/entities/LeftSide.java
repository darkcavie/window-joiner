package org.horus.window.joiner.entities;

public class LeftSide<K> extends JoinSide<K> {

    private JoinSide<K> rightSide;

    public void setRightSide(JoinSide<K> rightSide) {
        this.rightSide = rightSide;
    }

    public boolean isaMatchIn(long windowTime) {
        final long absoluteDiff;

        if(rightSide == null) {
            return false;
        }
        absoluteDiff = Math.abs(getTimestamp().toEpochMilli() - rightSide.getTimestamp().toEpochMilli());
        return absoluteDiff < windowTime;
    }

}
