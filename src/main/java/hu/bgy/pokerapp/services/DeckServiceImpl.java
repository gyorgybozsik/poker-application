package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.models.*;
import hu.bgy.pokerapp.repositories.CardRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class DeckServiceImpl implements DeckService {
    private final CardRepo cardRepo;
    private static final Random RANDOM = new Random();

    //public @NonNull Set<Card> createDeck() {
    //    final Set<Card> deck = new HashSet<>();
    //    stream(Symbol.values())
    //            .forEach(symbol -> stream(Rank.values())
    //                    .map(rank -> new Card(symbol, rank))
    //                    .forEach(deck::add));
    //    return deck;
    //}


    private @NonNull Card draw(@NonNull final Deck deck) {
        if (deck.isEmpty()) {
            throw new IllegalStateException("No card left in deck");
        }
        int x = RANDOM.nextInt(deck.size());

        return deck.getDeck().get(x);
    }

    @Override
    public @NonNull Card draw(final @NonNull Table table) {
        final Deck remainingDeck = remainingDeck(table);
        return draw(remainingDeck);
    }



    public @NonNull Deck remainingDeck(final @NonNull Table table) {
        final List<Card> allCard = cardRepo.findAll();
        final Set<Card> tableCards = table.getCardsForDeck();
        final Set<Card> playerCards = new HashSet<>();
        for (Player player : table.getSeats()) {
            Optional<Hand> hand = Optional.ofNullable(player.getHand());
            hand.ifPresent((value) -> playerCards.addAll(value.getCards()));
        }
        return new Deck(allCard, tableCards, playerCards);
    }
}
