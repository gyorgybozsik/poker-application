package hu.bgy.pokerapp.services.poker;

import hu.bgy.pokerapp.models.round.Round;

public abstract class Poker<ROUND extends Round> {
    private ROUND round;

}
