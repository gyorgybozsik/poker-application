package hu.bgy.pokerapp.services.poker;

import hu.bgy.pokerapp.models.round.Round;

public abstract class PokerGame<ROUND extends Round> {
    private ROUND round;

}
