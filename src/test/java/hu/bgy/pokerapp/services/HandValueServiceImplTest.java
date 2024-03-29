package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Hand;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static hu.bgy.pokerapp.enums.Rank.*;
import static hu.bgy.pokerapp.enums.Symbol.*;
import static hu.bgy.pokerapp.enums.Value.*;
import static java.util.Set.of;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HandValueServiceImplTest {
    private final HandValueService handValueService = new HandValueServiceImpl();

    public static Stream<Arguments> invalidValueTest() {
        return Stream.of(
                Arguments.of(of(card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN), card(HEARTH, NINE))),
                Arguments.of(of(card(HEARTH, TEN))),
                Arguments.of(of(card(HEARTH, TEN), card(HEARTH, JACK)))
        );
    }

    public static Stream<Arguments> validValueTest() {
        return Stream.of(
                Arguments.of(ROYAL_FLUSH, of(card(HEARTH, ACE), card(HEARTH, KING), card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN))),
                Arguments.of(ROYAL_FLUSH, of(card(HEARTH, TEN), card(HEARTH, ACE), card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, KING))),
                Arguments.of(ROYAL_FLUSH, of(card(CLUB, TEN), card(CLUB, ACE), card(CLUB, QUEEN), card(CLUB, JACK), card(CLUB, KING))),
                Arguments.of(STRAIGHT_FLUSH, of(card(HEARTH, NINE), card(HEARTH, KING), card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN))),
                Arguments.of(STRAIGHT_FLUSH, of(card(SPADE, NINE), card(SPADE, EIGHT), card(SPADE, QUEEN), card(SPADE, JACK), card(SPADE, TEN))),
                Arguments.of(POKER, of(card(HEARTH, ACE), card(SPADE, ACE), card(DIAMOND, ACE), card(CLUB, ACE), card(HEARTH, TEN))),
                Arguments.of(POKER, of(card(HEARTH, JACK), card(SPADE, JACK), card(DIAMOND, JACK), card(CLUB, JACK), card(HEARTH, ACE))),
                Arguments.of(FULL_HOUSE, of(card(SPADE, ACE), card(HEARTH, JACK), card(SPADE, JACK), card(DIAMOND, JACK), card(HEARTH, ACE))),
                Arguments.of(FULL_HOUSE, of(card(HEARTH, TEN), card(SPADE, TEN), card(HEARTH, ACE), card(DIAMOND, TEN), card(SPADE, ACE))),
                Arguments.of(FLUSH, of(card(HEARTH, TWO), card(HEARTH, ACE), card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN))),
                Arguments.of(FLUSH, of(card(SPADE, TWO), card(SPADE, ACE), card(SPADE, QUEEN), card(SPADE, JACK), card(SPADE, EIGHT))),
                Arguments.of(STRAIGHT, of(card(HEARTH, NINE), card(SPADE, KING), card(HEARTH, QUEEN), card(HEARTH, JACK), card(SPADE, TEN))),
                Arguments.of(STRAIGHT, of(card(HEARTH, NINE), card(CLUB, EIGHT), card(SPADE, QUEEN), card(SPADE, JACK), card(SPADE, TEN))),
                Arguments.of(DRILL, of(card(HEARTH, JACK), card(SPADE, JACK), card(DIAMOND, JACK), card(HEARTH, TWO), card(SPADE, ACE))),
                Arguments.of(DRILL, of(card(HEARTH, TEN), card(HEARTH, THREE), card(SPADE, ACE), card(SPADE, TEN), card(DIAMOND, TEN))),
                Arguments.of(TWO_PAIRS, of(card(SPADE, ACE), card(HEARTH, QUEEN), card(SPADE, JACK), card(DIAMOND, JACK), card(HEARTH, ACE))),
                Arguments.of(TWO_PAIRS, of(card(HEARTH, TEN), card(SPADE, TEN), card(HEARTH, ACE), card(DIAMOND, EIGHT), card(SPADE, ACE))),
                Arguments.of(PAIR, of(card(SPADE, KING), card(SPADE, QUEEN), card(SPADE, JACK), card(DIAMOND, JACK), card(HEARTH, ACE))),
                Arguments.of(PAIR, of(card(SPADE, ACE), card(HEARTH, QUEEN), card(SPADE, FOUR), card(SPADE, JACK), card(HEARTH, ACE))),
                Arguments.of(NOTHING, of(card(SPADE, KING), card(SPADE, QUEEN), card(SPADE, JACK), card(DIAMOND, THREE), card(HEARTH, ACE))),
                Arguments.of(NOTHING, of(card(SPADE, ACE), card(HEARTH, QUEEN), card(SPADE, FOUR), card(SPADE, JACK), card(HEARTH, TWO))),

                //more than 5 card
                Arguments.of(ROYAL_FLUSH, of(card(HEARTH, ACE), card(HEARTH, KING), card(DIAMOND, KING), card(CLUB, KING), card(SPADE, KING), card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN))),
                Arguments.of(ROYAL_FLUSH, of(card(HEARTH, TEN), card(SPADE, JACK), card(HEARTH, ACE), card(HEARTH, QUEEN), card(HEARTH, TWO), card(HEARTH, JACK), card(HEARTH, KING))),
                Arguments.of(ROYAL_FLUSH, of(card(CLUB, TEN), card(HEARTH, JACK), card(CLUB, ACE), card(CLUB, QUEEN), card(CLUB, JACK), card(CLUB, KING))),
                Arguments.of(STRAIGHT_FLUSH, of(card(HEARTH, NINE), card(HEARTH, KING), card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN), card(CLUB, ACE))),
                Arguments.of(STRAIGHT_FLUSH, of
                        (card(DIAMOND, KING), card(CLUB, KING), card(SPADE, KING),
                                card(HEARTH, NINE), card(HEARTH, KING), card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN),
                                card(HEARTH, TWO), card(SPADE, TWO),
                                card(SPADE, FOUR), card(SPADE, FIVE), card(SPADE, ACE), card(SPADE, TEN), card(SPADE, QUEEN),
                                card(DIAMOND, QUEEN), card(DIAMOND, ACE), card(CLUB, ACE)
                        )),
                Arguments.of(POKER, of(card(SPADE, ACE), card(HEARTH, ACE), card(HEARTH, QUEEN), card(HEARTH, JACK), card(SPADE, QUEEN), card(DIAMOND, QUEEN), card(CLUB, ACE), card(CLUB, QUEEN), card(DIAMOND, ACE), card(SPADE, JACK))),
                Arguments.of(POKER, of(card(SPADE, TWO), card(HEARTH, TWO), card(HEARTH, QUEEN), card(HEARTH, JACK), card(SPADE, QUEEN), card(DIAMOND, QUEEN), card(CLUB, TWO), card(CLUB, QUEEN), card(DIAMOND, TWO), card(HEARTH, TEN), card(CLUB, KING), card(SPADE, KING), card(DIAMOND, KING), card(HEARTH, KING))),
                Arguments.of(FULL_HOUSE, of(card(SPADE, TWO), card(HEARTH, TWO), card(HEARTH, TEN), card(CLUB, TWO),
                        card(SPADE, TEN), card(DIAMOND, THREE), card(CLUB, THREE), card(CLUB, QUEEN), card(DIAMOND, TEN))),
                Arguments.of(FULL_HOUSE, of(card(SPADE, TWO), card(HEARTH, TWO), card(HEARTH, TEN), card(CLUB, TWO), card(SPADE, TEN),
                        card(DIAMOND, THREE), card(CLUB, THREE), card(CLUB, QUEEN), card(DIAMOND, TEN), card(HEARTH, NINE),
                        card(CLUB, EIGHT), card(SPADE, KING), card(DIAMOND, KING), card(HEARTH, KING))),
                Arguments.of(FULL_HOUSE, of(card(SPADE, TWO), card(HEARTH, TWO),
                        card(HEARTH, TEN), card(CLUB, TWO), card(SPADE, TEN),
                        card(CLUB, THREE), card(CLUB, QUEEN), card(DIAMOND, TEN))),
                Arguments.of(DRILL, of(card(SPADE, ACE), card(HEARTH, QUEEN), card(CLUB, QUEEN), card(HEARTH, FIVE), card(SPADE, QUEEN), card(DIAMOND, TEN))),
                Arguments.of(TWO_PAIRS, of(card(SPADE, ACE), card(HEARTH, ACE), card(HEARTH, QUEEN), card(HEARTH, TEN), card(SPADE, QUEEN), card(DIAMOND, TEN), card(CLUB, FOUR), card(CLUB, FIVE), card(DIAMOND, FOUR), card(SPADE, FIVE))),
                Arguments.of(PAIR, of(card(SPADE, ACE), card(HEARTH, QUEEN), card(HEARTH, TEN), card(CLUB, FOUR), card(DIAMOND, FOUR), card(SPADE, FIVE))),
                Arguments.of(NOTHING, of(card(CLUB, FOUR), card(CLUB, TWO), card(CLUB, FIVE), card(DIAMOND, ACE), card(SPADE, TEN)))
        );
    }

    public static Stream<Arguments> getTest() {
        return Stream.of(
                Arguments.of(
                        of(
                                of(card(SPADE, ACE), card(SPADE, JACK), card(SPADE, TEN), card(SPADE, QUEEN), card(SPADE, KING)),
                                of(card(DIAMOND, KING), card(DIAMOND, QUEEN), card(DIAMOND, TEN), card(DIAMOND, ACE), card(DIAMOND, JACK))),
                        ROYAL_FLUSH,
                        of(card(SPADE, ACE), card(SPADE, JACK), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(SPADE, TEN),
                                card(SPADE, QUEEN), card(SPADE, KING), card(DIAMOND, TEN), card(DIAMOND, ACE), card(DIAMOND, JACK),
                                card(CLUB, ACE), card(HEARTH, TWO),
                                card(SPADE, FOUR), card(SPADE, SEVEN), card(SPADE, FIVE), card(SPADE, EIGHT), card(SPADE, SIX),
                                card(CLUB, TEN), card(CLUB, SEVEN), card(CLUB, NINE), card(CLUB, QUEEN))),
                Arguments.of(
                        of(
                                of(card(SPADE, NINE), card(SPADE, JACK), card(SPADE, TEN), card(SPADE, QUEEN), card(SPADE, KING)),
                                of(card(DIAMOND, KING), card(DIAMOND, QUEEN), card(DIAMOND, TEN), card(DIAMOND, NINE), card(DIAMOND, JACK))),
                        STRAIGHT_FLUSH,
                        of(card(SPADE, NINE), card(SPADE, JACK), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(SPADE, TEN),
                                card(SPADE, QUEEN), card(SPADE, KING), card(DIAMOND, TEN), card(DIAMOND, NINE), card(DIAMOND, JACK),
                                card(CLUB, ACE), card(HEARTH, TWO),
                                card(SPADE, FOUR), card(SPADE, SEVEN), card(SPADE, FIVE), card(SPADE, EIGHT), card(SPADE, SIX),
                                card(CLUB, TEN), card(CLUB, SEVEN), card(CLUB, NINE), card(CLUB, QUEEN))),
                Arguments.of(
                        of(
                                of(card(DIAMOND, EIGHT), card(CLUB, EIGHT), card(SPADE, EIGHT), card(HEARTH, EIGHT), card(DIAMOND, ACE))),
                        POKER,
                        of(card(DIAMOND, EIGHT), card(CLUB, FIVE), card(DIAMOND, TWO), card(DIAMOND, ACE), card(SPADE, TEN),
                                card(SPADE, EIGHT), card(HEARTH, FIVE), card(SPADE, TWO), card(CLUB, ACE),
                                card(CLUB, EIGHT), card(SPADE, FIVE), card(HEARTH, TWO),
                                card(HEARTH, EIGHT), card(DIAMOND, FIVE), card(CLUB, TWO), card(SPADE, ACE), card(CLUB, TEN))),
                Arguments.of(
                        of(
                                of(card(DIAMOND, JACK), card(SPADE, JACK), card(CLUB, JACK), card(DIAMOND, KING), card(SPADE, KING))),
                        FULL_HOUSE,
                        of(card(SPADE, NINE), card(SPADE, JACK), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(SPADE, TEN),
                                card(SPADE, QUEEN), card(SPADE, KING), card(DIAMOND, JACK), card(DIAMOND, NINE),
                                card(CLUB, ACE), card(HEARTH, TWO), card(CLUB, TWO), card(SPADE, TWO),
                                card(SPADE, FOUR), card(DIAMOND, SEVEN), card(SPADE, FIVE), card(SPADE, EIGHT), card(SPADE, SIX),
                                card(HEARTH, TEN), card(CLUB, SEVEN), card(CLUB, NINE), card(CLUB, JACK))),
                Arguments.of(
                        of(
                                of(card(DIAMOND, TWO), card(DIAMOND, JACK), card(DIAMOND, FIVE), card(DIAMOND, ACE), card(DIAMOND, QUEEN))),
                        FLUSH,
                        of(card(SPADE, NINE), card(SPADE, JACK), card(SPADE, TWO), card(SPADE, KING), card(SPADE, FIVE),
                                card(CLUB, FOUR), card(CLUB, QUEEN), card(CLUB, EIGHT), card(CLUB, SIX), card(CLUB, KING),
                                card(DIAMOND, TWO), card(DIAMOND, FIVE), card(DIAMOND, JACK), card(DIAMOND, QUEEN), card(DIAMOND, ACE))),
                Arguments.of(
                        of(
                                of(card(CLUB, TWO), card(SPADE, THREE), card(DIAMOND, FIVE), card(DIAMOND, FOUR), card(DIAMOND, SIX))),
                        STRAIGHT,
                        of(card(CLUB, TWO), card(HEARTH, TWO), card(SPADE, TWO), card(SPADE, THREE), card(DIAMOND, FIVE),
                                card(DIAMOND, FOUR), card(DIAMOND, SIX), card(CLUB, ACE), card(CLUB, KING))),
                Arguments.of(
                        of(
                                of(card(DIAMOND, JACK), card(SPADE, JACK), card(CLUB, JACK), card(CLUB, ACE), card(SPADE, KING))),
                        DRILL,
                        of(card(SPADE, NINE), card(SPADE, JACK), card(SPADE, TEN), card(SPADE, KING), card(DIAMOND, JACK),
                                card(CLUB, ACE), card(SPADE, TWO),
                                card(SPADE, FOUR), card(SPADE, FIVE), card(SPADE, EIGHT), card(SPADE, SIX),
                                card(CLUB, SEVEN), card(CLUB, JACK))),
                Arguments.of(
                        of(
                                of(card(DIAMOND, JACK), card(SPADE, JACK), card(CLUB, NINE), card(CLUB, ACE), card(SPADE, NINE))),
                        TWO_PAIRS,
                        of(card(SPADE, JACK), card(HEARTH, FIVE), card(HEARTH, KING), card(DIAMOND, JACK),
                                card(CLUB, ACE), card(SPADE, TWO),
                                card(SPADE, NINE), card(SPADE, FIVE), card(HEARTH, TWO), card(DIAMOND, TEN),
                                card(CLUB, NINE))),
                Arguments.of(
                        of(of(card(DIAMOND, JACK), card(SPADE, JACK), card(HEARTH, FIVE), card(CLUB, ACE), card(HEARTH, KING))),
                        PAIR,
                        of(card(SPADE, JACK), card(HEARTH, FIVE), card(HEARTH, KING), card(DIAMOND, JACK), card(CLUB, ACE))),
                Arguments.of(
                        of(
                                of(card(CLUB, ACE), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(SPADE, JACK), card(SPADE, NINE))),
                        NOTHING,
                        of(card(SPADE, NINE), card(SPADE, JACK), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(CLUB, TWO),
                                card(SPADE, THREE), card(DIAMOND, FIVE), card(DIAMOND, EIGHT),
                                card(CLUB, ACE), card(HEARTH, SEVEN)))

        );
    }

    @ParameterizedTest
    @MethodSource(value = "invalidValueTest")
    void testDifferentHandsWithInvalidInputs(final Set<Card> cards) {
        assertThrows(IllegalArgumentException.class, () -> handValueService.evaluate(hand(cards)));
    }

    @ParameterizedTest
    @MethodSource(value = "validValueTest")
    void testDifferentHandsWithValidInputs(final Value expected, final Set<Card> cards) {
        assertEquals(expected, handValueService.evaluate(hand(cards)));
    }

    @ParameterizedTest
    @MethodSource(value = "getTest")
    void testGetValue(final Set<TreeSet<Card>> expected, final Value value, final Set<Card> cards) {
        Set<TreeSet<Card>> handBasedOnValue = handValueService.getHandBasedOnValue(value, hand(cards));
        assertTrue(handBasedOnValue.containsAll(expected));
        assertEquals(expected, handBasedOnValue);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testDifferentHands(final Set<Card> cards) {
        assertThrows(IllegalArgumentException.class, () -> handValueService.evaluate(new Hand(cards)));
    }

    private static Card card(final Symbol symbol, final Rank rank) {
        return new Card(symbol, rank);
    }

    private Hand hand(final Set<Card> cards) {
        return new Hand(cards);
    }
}
