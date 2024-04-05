package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.PlayerState;

public class Player {
    private PlayerState state;

    public boolean isActive() {
        return PlayerState.ACTIVE.equals(state);
    }
}
