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

    public static Rank getHighest() {
        return values()[0];
    }

    public int distance(@NonNull final Rank rank) {
        return Math.abs(ordinal() - rank.ordinal());
    }

    public boolean isHighest() {
        return this.ordinal() == 0;
    }

    public boolean isHigher(@NonNull final Rank previousHighest) {
        return this.ordinal() < previousHighest.ordinal();
    }
}
