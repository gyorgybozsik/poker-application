package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.models.round.Round;
import hu.bgy.pokerapp.services.poker.PokerGame;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;

@RequiredArgsConstructor
public class Table <ROUND extends Round, POKER extends PokerGame<ROUND>> {
    private final POKER poker;
    final Deque<Player> seats = new ArrayDeque<>();

    /**
     * elsőnek feltöltjük valahogy a listát
     * ennek a listának mindig a legelső eleme a kisvak
     * a legutolsó eleme a dealer
     * amikor kezdődik a kör, akkor végigmész a listán és egy másik dequeuejhoz hozzáadod sorrendben
     * ha vége az adott menet, akkor itt lépteti eggyel az játokosokat
     */

    private void rotatePlayers() {
        final Player player = seats.pollFirst();
        seats.addLast(player);

    }
}
