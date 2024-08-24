package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;

public interface DeckService {


   // @NonNull Set<Card> createDeck();

    @NonNull Card draw(@NonNull final Table table);
}
