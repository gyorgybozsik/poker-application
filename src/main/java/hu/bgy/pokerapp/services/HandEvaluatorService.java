package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Hand;
import lombok.NonNull;

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

        return switch (value) {
            case ROYAL_FLUSH, STRAIGHT_FLUSH -> checkRoyalOrStraightFlush(value, hand);
            case POKER -> hand.isQuad();
            case FULL_HOUSE -> checkFullHouse(hand);
            case FLUSH -> false;
            case STRAIT -> false;
            case DRILL -> false;
            case TWO_PAIRS -> false;
            case PAIR -> false;
            case NOTHING -> false;
        };
        //return (value.getNumberOfPairs() != 0 ? value.getNumberOfPairs() == hand.getNumberOfPairs() : true) &&
        //        (value.isDrill() ? hand.isDrill() : true) &&
        //        (value.isQuad() ? hand.isQuad() : true) &&
        //        (value.isStraight() ? hand.isStraight() : true) &&
        //        (value.isFlush() ? hand.isFlush() : true) &&
        //        (value.isStraight() && value.isFlush() ? hand.isRoyalOrStraitFlush(value.isHighest()) : true);
    }

    private boolean checkFullHouse(@NonNull final Hand hand) {
        return hand.isFullHouse();
    }
    private boolean checkRoyalOrStraightFlush(@NonNull final Value value, @NonNull final Hand hand) {
        return !value.isStraight() || !value.isFlush() || hand.isRoyalOrStraitFlush(value.isHighest());
    }
}
