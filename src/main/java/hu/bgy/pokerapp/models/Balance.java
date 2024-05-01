package hu.bgy.pokerapp.models;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class Balance {
    private BigDecimal cash;
    private BigDecimal bet;

    public Balance(BigDecimal cash) {
        this.cash = cash;
        bet = BigDecimal.ZERO;
    }

    public void bet(@NonNull final BigDecimal currentBetAmount) {
        if (currentBetAmount.compareTo(cash) > -1) {
            bet = bet.add(cash);
            cash = BigDecimal.ZERO;
        } else {
            bet = bet.add(currentBetAmount);
            cash = cash.subtract(currentBetAmount);
        }

    }
}
