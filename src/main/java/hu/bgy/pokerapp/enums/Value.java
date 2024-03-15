package hu.bgy.pokerapp.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Value {

    ROYAL_FLUSH(0, false, false, true, true, true),
    STRAIGHT_FLUSH(0, false, false, true, true, false),
    POKER(0, false, true, false, false, false),
    FULL_HOUSE(1, true, false, false, false, false),
    FLUSH(0, false, false, false, true, false),
    STRAIGHT(0, false, false, true, false, false),
    DRILL(0, true, false, false, false, false),
    TWO_PAIRS(2, false, false, false, false, false),
    PAIR(1, false, false, false, false, false),
    NOTHING(0, false, false, false, false, false);

    private final int numberOfPairs;
    private final boolean drill;
    private final boolean quad;
    private final boolean isStraight;
    private final boolean isFlush;
    private final boolean isHighest;

}
