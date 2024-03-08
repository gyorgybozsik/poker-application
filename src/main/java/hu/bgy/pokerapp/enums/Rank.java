package hu.bgy.pokerapp.enums;

import lombok.NonNull;

public enum Rank {
    ACE,
    KING,
    QUEEN,
    JACK,
    TEN,
    NINE,
    EIGHT,
    SEVEN,
    SIX,
    FIVE,
    FOUR,
    THREE,
    TWO,
    ;

    public int distance(@NonNull final Rank rank){
        return Math.abs(ordinal() - rank.ordinal());
    }
}
