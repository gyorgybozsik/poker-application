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
import java.util.stream.Stream;

import static hu.bgy.pokerapp.enums.Rank.*;
import static hu.bgy.pokerapp.enums.Symbol.*;
import static hu.bgy.pokerapp.enums.Value.*;
import static java.util.Set.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class HandEvaluatorServiceTest {
    private final HandEvaluatorService handEvaluatorService = new HandEvaluatorService();

    public static Stream<Arguments> fromInvalidMethod() {
        return Stream.of(
                Arguments.of(of(card(HEARTH, QUEEN), card(HEARTH, JACK), card(HEARTH, TEN), card(HEARTH, NINE))),
                Arguments.of(of(card(HEARTH, TEN))),
                Arguments.of(of(card(HEARTH, TEN), card(HEARTH, JACK)))
        );
    }

    public static Stream<Arguments> fromValidMethod() {
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
                Arguments.of(STRAIT, of(card(HEARTH, NINE), card(SPADE, KING), card(HEARTH, QUEEN), card(HEARTH, JACK), card(SPADE, TEN))),
                Arguments.of(STRAIT, of(card(HEARTH, NINE), card(CLUB, EIGHT), card(SPADE, QUEEN), card(SPADE, JACK), card(SPADE, TEN))),
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
                //Arguments.of(FULL_HOUSE, of(card(SPADE, TWO), card(HEARTH, TWO), card(HEARTH, TEN), card(CLUB, TWO), card(SPADE, TEN), card(DIAMOND, THREE), card(CLUB, THREE), card(CLUB, QUEEN), card(DIAMOND, TEN))),
                //Arguments.of(FULL_HOUSE, of(card(SPADE, TWO), card(HEARTH, TWO), card(HEARTH, TEN), card(CLUB, TWO), card(SPADE, TEN), card(DIAMOND, THREE), card(CLUB, THREE), card(CLUB, QUEEN), card(DIAMOND, TEN), card(HEARTH, NINE), card(CLUB, EIGHT), card(SPADE, KING), card(DIAMOND, KING), card(HEARTH, KING))),
                //Arguments.of(DRILL, of(card(SPADE, ACE), card(HEARTH, QUEEN), card(CLUB, QUEEN), card(HEARTH, FIVE), card(SPADE, QUEEN), card(DIAMOND, TEN))),
                //Arguments.of(TWO_PAIRS, of(card(SPADE, ACE), card(HEARTH, ACE), card(HEARTH, QUEEN), card(HEARTH, TEN), card(SPADE, QUEEN), card(DIAMOND, TEN), card(CLUB, FOUR), card(CLUB, FIVE), card(DIAMOND, FOUR), card(SPADE, FIVE))),

                Arguments.of(NOTHING, of(card(CLUB, FOUR), card(CLUB, TWO), card(CLUB, FIVE), card(DIAMOND, ACE), card(SPADE, TEN)))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "fromInvalidMethod")
    void testDifferentHandsWithInvalidInputs(final Set<Card> cards) {
        assertThrows(IllegalArgumentException.class, () -> handEvaluatorService.evaluate(hand(cards)));
    }

    @ParameterizedTest
    @MethodSource(value = "fromValidMethod")
    void testDifferentHandsWithValidInputs(final Value expected, final Set<Card> cards) {
        assertEquals(expected, handEvaluatorService.evaluate(hand(cards)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testDifferentHands(final Set<Card> cards) {
        assertThrows(IllegalArgumentException.class, () -> handEvaluatorService.evaluate(new Hand(cards)));
    }

    private static Card card(final Symbol symbol, final Rank rank) {
        return new Card(symbol, rank);
    }

    private Hand hand(final Set<Card> cards) {
        return new Hand(cards);
    }
}
