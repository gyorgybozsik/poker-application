package hu.bgy.pokerapp.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Balance {
    private BigDecimal cash;
    private BigDecimal bet;

    public Balance(BigDecimal cash) {
        this.cash = cash;
        bet = BigDecimal.ZERO;
    }
}
