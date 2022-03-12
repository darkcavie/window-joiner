package org.horus.window.joiner;

public interface JoinerUseCase<K> {

    void receiveLeftSide(TimeWindowed<K> leftSide);

    void receiveRightSide(TimeWindowed<K> rightSide);

}
