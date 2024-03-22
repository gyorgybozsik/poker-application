package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Deck {
    public static Set<Card> deck = new HashSet<>();

    public Deck() {
        fillDeck();
        listCards(deck);
        System.out.println(deck.size());
    }


    public void listCards(Set<Card> cards) {
        for (Card card : cards) {System.out.println(card.print());}
    }

    void fillDeck() {
        Arrays
                .stream(Symbol.values())
                .forEach(symbol -> Arrays
                        .stream(Rank.values())
                        .forEach(rank -> deck.add(new Card(symbol, rank))));

    }
}
