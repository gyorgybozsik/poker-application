package hu.bgy.pokerapp.models.round;

import hu.bgy.pokerapp.models.Player;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

public abstract class Round {
    final Deque<Player> players;

    public Round(final Deque<Player> players) {
        this.players = players.stream()
                .filter(Player::isActive)
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
