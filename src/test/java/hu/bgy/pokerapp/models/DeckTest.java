package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static hu.bgy.pokerapp.enums.Rank.*;
import static hu.bgy.pokerapp.enums.Symbol.*;
import static hu.bgy.pokerapp.services.HandValueServiceImplTest.card;

public class DeckTest {
    Set<Card> all = Set.of(
            card(HEARTH, ACE), card(HEARTH, KING), card(HEARTH, QUEEN), card(HEARTH, JACK),
            card(HEARTH, TEN), card(HEARTH, NINE), card(HEARTH, EIGHT), card(HEARTH, SEVEN),
            card(HEARTH, SIX), card(HEARTH, FIVE), card(HEARTH, FOUR), card(HEARTH, THREE), card(HEARTH, TWO),
            card(SPADE, ACE), card(SPADE, KING), card(SPADE, QUEEN), card(SPADE, JACK),
            card(SPADE, TEN), card(SPADE, NINE), card(SPADE, EIGHT), card(SPADE, SEVEN),
            card(SPADE, SIX), card(SPADE, FIVE), card(SPADE, FOUR), card(SPADE, THREE), card(SPADE, TWO),
            card(CLUB, ACE), card(CLUB, KING), card(CLUB, QUEEN), card(CLUB, JACK),
            card(CLUB, TEN), card(CLUB, NINE), card(CLUB, EIGHT), card(CLUB, SEVEN),
            card(CLUB, SIX), card(CLUB, FIVE), card(CLUB, FOUR), card(CLUB, THREE), card(CLUB, TWO),
            card(DIAMOND, ACE), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(DIAMOND, JACK),
            card(DIAMOND, TEN), card(DIAMOND, NINE), card(DIAMOND, EIGHT), card(DIAMOND, SEVEN),
            card(DIAMOND, SIX), card(DIAMOND, FIVE), card(DIAMOND, FOUR), card(DIAMOND, THREE), card(DIAMOND, TWO));

    public static Stream<Arguments> randomCards() {
        return Stream.of(
                Arguments.of(Set.of(card(HEARTH, Rank.TEN)))
        );
    }

    @Test
    void createDeckTest() {
        final Deck deck = new Deck();
        Assertions.assertNotNull(deck);
        Assertions.assertEquals(Rank.values().length * Symbol.values().length, deck.size());
    }

    @ParameterizedTest
    @MethodSource("randomCards")
    void checkCardsExistence(final Set<Card> cards) {
        final Deck deck = new Deck();
        Assertions.assertTrue(deck.getDeck().containsAll(cards));
    }

    @RepeatedTest(10)
    void checkCardExistence() {
        final Deck deck = new Deck();
        final Card card = deck.draw();
        Assertions.assertNotNull(card);
        Assertions.assertFalse(deck.getDeck().contains(card));
    }
}
