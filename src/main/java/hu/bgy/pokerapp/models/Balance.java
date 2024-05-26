package hu.bgy.pokerapp.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "balances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "cash")
    private BigDecimal cash;

    @Column(name = "bet")
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
//todo táblák létrehozása és összekapcsolása a hogyhívják kóddal :P