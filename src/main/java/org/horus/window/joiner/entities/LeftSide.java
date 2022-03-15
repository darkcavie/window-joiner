package org.horus.window.joiner.entities;

public class LeftSide<K> extends JoinSide<K> {

    private JoinSide<K> rightSide;

    public void setRightSide(JoinSide<K> rightSide) {
        this.rightSide = rightSide;
    }

    public boolean isaMatchIn(long period) {
        final long absoluteDiff;

        if(period < 0) {
            throw new IllegalArgumentException("The period must be positive");
        }
        if(rightSide == null) {
            return false;
        }
        absoluteDiff = Math.abs(getTimestamp().toEpochMilli() - rightSide.getTimestamp().toEpochMilli());
        return absoluteDiff < period;
    }

}
