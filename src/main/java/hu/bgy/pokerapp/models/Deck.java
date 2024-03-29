package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import lombok.Getter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

@Getter
public class Deck {
    private final Set<Card> deck = new HashSet<>();
    private static final Random RANDOM = new Random();

    public Deck() {
        fillDeck();
    }

    void fillDeck() {
        stream(Symbol.values())
                .forEach(symbol -> stream(Rank.values())
                        .forEach(addCard(symbol)));

    }

    private Consumer<Rank> addCard(Symbol symbol) {
        return rank -> deck.add(new Card(symbol, rank));
    }

    public int size() {
        return deck.size();
    }


    public Card draw() {
        int x = RANDOM.nextInt(deck.size());
        for (Card card : deck) {
            if (x-- == 0) {
                deck.remove(card);
                return card;
            }
        }
        throw new IllegalArgumentException();
    }
}
