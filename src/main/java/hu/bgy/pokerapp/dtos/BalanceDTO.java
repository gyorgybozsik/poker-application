package hu.bgy.pokerapp.dtos;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record BalanceDTO(@NonNull BigDecimal cash,
                         @NonNull BigDecimal bet) {

}
