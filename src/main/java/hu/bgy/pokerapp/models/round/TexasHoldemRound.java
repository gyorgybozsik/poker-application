package hu.bgy.pokerapp.models.round;

import hu.bgy.pokerapp.models.Player;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Deque;

import static hu.bgy.pokerapp.models.round.TexasHoldemState.*;


public class TexasHoldemRound extends Round {
    private TexasHoldemState state;

    public TexasHoldemRound(@NonNull final Deque<Player> players,
                            final @NonNull BigDecimal smallBlind) {
        super(players, smallBlind);
        state = PRE_FLOP;
        Player smallBlindP = players.pollFirst();
        smallBlindP.bet(smallBlind);
        players.add(smallBlindP);
        Player bigBlind = players.pollFirst();
        bigBlind.bet(smallBlind.multiply(BigDecimal.TWO));
        players.add(bigBlind);

    }

}
