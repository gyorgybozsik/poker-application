package hu.bgy.pokerapp.dtos;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import lombok.Builder;
import lombok.NonNull;


@Builder
public record PlayerStateDTO(@NonNull InGameState inGameState, @NonNull RoundRole roundRole) {

}
