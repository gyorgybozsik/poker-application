package hu.bgy.pokerapp.models.poker;

import hu.bgy.pokerapp.enums.PokerType;
import lombok.NonNull;

public interface Poker {
    boolean isPokerKind(final @NonNull PokerType poker);
}
