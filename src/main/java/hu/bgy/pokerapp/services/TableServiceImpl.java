package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.round.Round;
import hu.bgy.pokerapp.services.poker.PokerGame;
import hu.bgy.pokerapp.services.poker.TableService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;

@RequiredArgsConstructor
public class TableServiceImpl<ROUND extends Round, POKER extends PokerGame<ROUND>> implements TableService {
    private final POKER poker;

    /**
     * elsőnek feltöltjük valahogy a listát
     * ennek a listának mindig a legelső eleme a kisvak
     * a legutolsó eleme a dealer
     * amikor kezdődik a kör, akkor végigmész a listán és egy másik dequeuejhoz hozzáadod sorrendben
     * ha vége az adott menet, akkor itt lépteti eggyel az játokosokat
     */
}
