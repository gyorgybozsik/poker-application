package hu.bgy.pokerapp.dtos;

import hu.bgy.pokerapp.enums.PlayerAction;
import lombok.NonNull;

import java.math.BigDecimal;

public record SpeakerActionDTO(@NonNull Long playerId,
                               @NonNull PlayerAction playerAction,
                               @NonNull BigDecimal changeAmount) {

    public SpeakerActionDTO(
            @NonNull Long playerId,
            @NonNull PlayerAction playerAction,
            @NonNull BigDecimal changeAmount) {
        this.playerId = playerId;
        this.playerAction = playerAction;
        switch (playerAction) {
            case RAISE, CALL -> {
                if (BigDecimal.ZERO.compareTo(changeAmount) < 0)
                    throw new IllegalArgumentException();
                this.changeAmount = changeAmount;
            }
            default -> this.changeAmount = BigDecimal.ZERO;
        }
    }
}
