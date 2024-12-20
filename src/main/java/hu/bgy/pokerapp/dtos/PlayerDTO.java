package hu.bgy.pokerapp.dtos;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;
import java.util.UUID;

public record PlayerDTO( UUID id,
                        @NonNull String name,
                        @Nullable BalanceDTO balance,
                        @Nullable Set<CardDTO> cards,
                        @Nullable PlayerStateDTO playerState) {

}
