package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Hand;

import static hu.bgy.pokerapp.enums.Value.values;
import static java.util.Arrays.stream;

public class HandEvaluator {
    public Value evaluate(final Hand hand) {
        if (hand == null) {
            throw new IllegalArgumentException();
        }
        return stream(values())
                .filter(value -> isMatch(value, hand))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isMatch(final Value value, final Hand hand) {
        return value.getNumberOfPairs() == hand.getNumberOfPairs() &&
                value.isDrill() == hand.isDrill() &&
                value.isQuad() == hand.isQuad() &&
                value.isStraight() == hand.isStraight() &&
                value.isFlush() == hand.isFlush() &&
                (!value.isHighest() || hand.isHighest());
    }
}
