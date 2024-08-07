package hu.bgy.pokerapp.dtos;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public record PlayerDTO(@NonNull Long id,
                        @NonNull String name,
                        @Nullable BalanceDTO balance,
                        @Nullable PlayerStateDTO playerState) {

}
