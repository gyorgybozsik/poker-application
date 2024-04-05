package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Deck;
import lombok.NonNull;

import java.util.Set;

public interface DeckService {


    @NonNull Set<Card> createDeck();


    @NonNull Card draw(@NonNull final Deck deck);
}
