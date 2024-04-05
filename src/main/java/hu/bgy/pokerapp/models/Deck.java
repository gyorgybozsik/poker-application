package hu.bgy.pokerapp.models;

import lombok.Getter;
import lombok.NonNull;

import java.util.Set;

@Getter
public class Deck {
    private final Set<Card> deck;

    public Deck(@NonNull final Set<Card> deck) {
        this.deck = deck;
    }

    public int size() {
        return deck.size();
    }

    public boolean remove(@NonNull final Card card) {
        return deck.remove(card);
    }

}
