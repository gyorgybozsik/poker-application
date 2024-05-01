package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.PlayerState;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class Player {

    private String name;
    private Balance balance;
    private PlayerState state;

    public Player(
            @NonNull final String name,
            @NonNull final BigDecimal cash) {
        this.name = name;
        this.balance = new Balance(cash);

        state = PlayerState.ACTIVE;
    }

    public boolean isActive() {
        return PlayerState.ACTIVE.equals(state);
    }

    public void bet(@NonNull final BigDecimal betAmount) {
        balance.bet(betAmount);
    }
}
