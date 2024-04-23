package hu.bgy.pokerapp.models;

import lombok.Getter;
import lombok.Setter;

import java.util.TreeSet;

import static org.springframework.util.CollectionUtils.isEmpty;

@Getter
public class Hand {
    private final TreeSet<Card> cards;
    @Setter
    private HandState handState;

    public Hand(final TreeSet<Card> cards) {
        this.cards = cards;
        validate();
    }


    public void validate() {
        if (isEmpty(cards) || cards.size() < 5) {
            throw new IllegalArgumentException();
        }
    }
}
