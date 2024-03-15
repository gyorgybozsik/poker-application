package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Hand;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;

public interface HandValueService {
    @NonNull Value evaluate(@Nullable final Hand hand);
    @NonNull Set<Card> getHand(@NonNull final Hand hand);
}
