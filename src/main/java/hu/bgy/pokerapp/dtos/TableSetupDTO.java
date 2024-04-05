package hu.bgy.pokerapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.bgy.pokerapp.enums.PokerType;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public record TableSetupDTO(
        @JsonProperty("poker") @NonNull PokerType pokerType,
        @NonNull BigDecimal cash,
        @NonNull BigDecimal smallBlind,
        @NonNull List<PlayerDTO> players) {
}
