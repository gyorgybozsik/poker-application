package hu.bgy.pokerapp.models;

import lombok.Getter;
import lombok.NonNull;

import java.util.TreeSet;

import static java.util.Arrays.stream;

@Getter
public class HandEvaluation {
    private final TreeSet<Card> cards;


    private final HandState handState;

    public HandEvaluation(final @NonNull TreeSet<Card> @NonNull ... setsOfCards) {
        this.cards = new TreeSet<>();
        stream(setsOfCards)
                .forEach(this.cards::addAll);
        handState = new HandState(this.cards);
    }
}
