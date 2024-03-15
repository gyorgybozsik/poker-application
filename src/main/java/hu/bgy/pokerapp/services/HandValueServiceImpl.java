package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Hand;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;
import java.util.TreeSet;

import static hu.bgy.pokerapp.enums.Value.values;
import static java.util.Arrays.stream;

public class HandValueServiceImpl implements HandValueService {
    public @NonNull Value evaluate(@Nullable final Hand hand) {
        if (hand == null) {
            throw new IllegalArgumentException();
        }
        return stream(values())
                .filter(value -> isMatch(value, hand))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public @NonNull Set<Card> getValuesHand(@NonNull final Hand hand) {
        final Value value = evaluate(hand);
        final Set<TreeSet<Card>> hands = getHandBasedOnValue(value, hand);
        return hands.stream()
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public @NonNull Set<TreeSet<Card>> getHandBasedOnValue(@NonNull final Value value, @NonNull final Hand hand) {
        return switch (value){
            case ROYAL_FLUSH -> hand.getRoyalOrStraightFlush(true);
            case STRAIGHT_FLUSH -> null;
            case POKER -> null;
            case FULL_HOUSE -> null;
            case FLUSH -> null;
            case STRAIGHT -> null;
            case DRILL -> null;
            case TWO_PAIRS -> null;
            case PAIR -> null;
            case NOTHING -> null;
        };
    }

    private boolean isMatch(final Value value, final Hand hand) {

        return switch (value) {
            case ROYAL_FLUSH, STRAIGHT_FLUSH -> checkRoyalOrStraightFlush(value, hand);
            case POKER -> hand.isQuad();
            case FULL_HOUSE -> hand.isFullHouse();
            case FLUSH -> hand.isFlush();
            case STRAIGHT -> hand.isStraight();
            case DRILL -> hand.isDrill();
            case TWO_PAIRS -> hand.isTwoPair();
            case PAIR -> hand.isPair();
            case NOTHING -> hand.isNothing();
        };
        //return (value.getNumberOfPairs() != 0 ? value.getNumberOfPairs() == hand.getNumberOfPairs() : true) &&
        //        (value.isDrill() ? hand.isDrill() : true) &&
        //        (value.isQuad() ? hand.isQuad() : true) &&
        //        (value.isStraight() ? hand.isStraight() : true) &&
        //        (value.isFlush() ? hand.isFlush() : true) &&
        //        (value.isStraight() && value.isFlush() ? hand.isRoyalOrStraitFlush(value.isHighest()) : true);
    }

    private boolean checkRoyalOrStraightFlush(@NonNull final Value value, @NonNull final Hand hand) {
        return !value.isStraight() || !value.isFlush() || hand.isRoyalOrStraitFlush(value.isHighest());
    }
}
