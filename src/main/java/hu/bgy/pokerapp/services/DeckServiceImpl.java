package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Deck;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static java.util.Arrays.stream;

@Service
public class DeckServiceImpl implements DeckService {
    private static final Random RANDOM = new Random();

    public @NonNull Set<Card> createDeck() {
        final Set<Card> deck = new HashSet<>();
        stream(Symbol.values())
                .forEach(symbol -> stream(Rank.values())
                        .map(rank -> new Card(symbol, rank))
                        .forEach(deck::add));
        return deck;
    }


    public @NonNull Card draw(@NonNull final Deck deck) {
        int x = RANDOM.nextInt(deck.size());
        for (Card card : deck.getDeck()) {
            if (x-- == 0) {
                deck.remove(card);
                return card;
            }
        }
        throw new IllegalArgumentException();
    }
}
