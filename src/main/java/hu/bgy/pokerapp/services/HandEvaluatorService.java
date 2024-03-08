package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Hand;

import static hu.bgy.pokerapp.enums.Value.values;
import static java.util.Arrays.stream;

public class HandEvaluatorService {
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
        return (value.getNumberOfPairs() != 0 ? value.getNumberOfPairs() == hand.getNumberOfPairs() : true) &&
                (value.isDrill() ? hand.isDrill() : true) &&
                (value.isQuad() ? hand.isQuad() : true) &&
                (value.isStraight() ? hand.isStraight() : true) &&
                (value.isFlush() ? hand.isFlush() : true) &&
                (value.isStraight() && value.isFlush() ? hand.isStraitFlush() : true) &&
                (!value.isHighest() || hand.isHighest());
    }
}
