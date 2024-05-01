package hu.bgy.pokerapp.models.round;

import hu.bgy.pokerapp.models.Player;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

public abstract class Round {
    final Deque<Player> players;
    final BigDecimal smallBlind;

    public Round(final @NonNull Deque<Player> players,
                 final @NonNull BigDecimal smallBlind) {
        this.players = players.stream()
                .filter(Player::isActive)
                .collect(Collectors.toCollection(LinkedList::new));
        this.smallBlind = smallBlind;
    }

}
