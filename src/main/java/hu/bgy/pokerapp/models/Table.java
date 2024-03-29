package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.services.poker.PokerGame;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;

@RequiredArgsConstructor
public class Table <POKER extends PokerGame> {
    private final POKER poker;
    Deque<Player> seats = new ArrayDeque<>();

    /**
     * elsőnek feltöltjük valahogy a listát
     * ennek a listának mindig a legelső eleme a kisvak
     * a legutolsó eleme a dealer
     * amikor kezdődik a kör, akkor végigmész a listán és egy másik dequeuejhoz hozzáadod sorrendben
     * ha vége az adott menet, akkor itt lépteti eggyel az játokosokat
     */

    private void rotatePlayers() {
        Player player = seats.pollFirst();
        seats.addLast(player);

    }
}
