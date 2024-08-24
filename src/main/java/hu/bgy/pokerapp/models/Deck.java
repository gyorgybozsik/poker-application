package hu.bgy.pokerapp.models;

import lombok.Getter;
import lombok.NonNull;

import java.util.*;

@Getter
public class Deck {
    private final List<Card> deck = new ArrayList<>();

    @SafeVarargs
    public Deck(final @NonNull List<Card> allCard, final @NonNull Set<Card>... used) {
        deck.addAll(allCard);
        Arrays.stream(used).forEach(deck::removeAll);
    }

    public int size() {
        return deck.size();
    }

    public boolean remove(@NonNull final Card card) {
        return deck.remove(card);
    }

    public  boolean isEmpty(){
        return deck.isEmpty();
    }
}
