package hu.bgy.pokerapp.models;

import java.util.ArrayDeque;
import java.util.Deque;

public class Table {
    final Deque<Player> seats = new ArrayDeque<>();

    private void rotatePlayers() {
        final Player player = seats.pollFirst();
        seats.addLast(player);
    }
}
