package hu.bgy.pokerapp.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "balances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // @SequenceGenerator(name = "b_seq_g", sequenceName = "balances_id_seq")
    private UUID id;

    @Column(name = "cash")
    private BigDecimal cash;

    @Column(name = "bet")
    private BigDecimal bet;

    @ToString.Exclude
    @OneToOne(mappedBy = "balance")
    private Player player;

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

    public void addCash(BigDecimal cash) {
        this.cash = this.cash.add(cash);
    }

    public boolean hasCash() {
        return cash.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal deductBet(BigDecimal minimumBet) {
        if (bet.compareTo(minimumBet) == -1) {
            BigDecimal tmp = bet;
            bet = BigDecimal.ZERO;
            return tmp;
        }
        bet = bet.subtract(minimumBet);
        return minimumBet;
    }
}
