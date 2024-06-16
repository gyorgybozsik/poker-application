package hu.bgy.pokerapp.models.poker;

import hu.bgy.pokerapp.enums.PokerType;
import lombok.NonNull;

public class TexasHoldem implements Poker {
    @Override
    public boolean isPokerKind(@NonNull PokerType pokerType) {
        return PokerType.TEXAS_HOLDEM.equals(pokerType);
    }
    // private TexasHoldemState state;
//
   // public TexasHoldemRound(@NonNull final Deque<Player> players,
   //                         final @NonNull BigDecimal smallBlind) {
   //     super(players, smallBlind);
   //     state = PRE_FLOP;
   //     Player smallBlindP = players.pollFirst();
   //     smallBlindP.bet(smallBlind);
   //     players.add(smallBlindP);
   //     Player bigBlind = players.pollFirst();
   //     bigBlind.bet(smallBlind.multiply(BigDecimal.TWO));
   //     players.add(bigBlind);
   // }
}
