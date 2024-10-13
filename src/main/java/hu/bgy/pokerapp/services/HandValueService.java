package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Hand;
import hu.bgy.pokerapp.models.Player;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface HandValueService {
    @NonNull Value evaluate(@Nullable final Hand hand);

    @NonNull Set<TreeSet<Card>> getHandBasedOnValue(@NonNull final Value value, @NonNull final Hand hand);

    @NonNull Set<Card> getValuesHand(@NonNull final Hand hand);

    List<List<Player>> orderWithHighestCard(Value key, List<Player> value);
}
