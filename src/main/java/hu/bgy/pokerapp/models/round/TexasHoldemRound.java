package hu.bgy.pokerapp.models.round;

import hu.bgy.pokerapp.models.Player;
import lombok.NonNull;

import java.util.Deque;


public class TexasHoldemRound extends Round {
    public TexasHoldemRound(@NonNull final Deque<Player> players) {
        super(players);
    }
}
