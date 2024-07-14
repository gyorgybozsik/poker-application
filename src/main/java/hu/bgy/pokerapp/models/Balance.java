package hu.bgy.pokerapp.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "balances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    // @SequenceGenerator(name = "b_seq_g", sequenceName = "balances_id_seq")
    private Long id;

    @Column(name = "cash")
    private BigDecimal cash;

    @Column(name = "bet")
    private BigDecimal bet;

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

    public boolean hasCash() {
        return cash.compareTo(BigDecimal.ZERO) > 0;
    }
}


//todo táblák létrehozása és összekapcsolása a hogyhívják kóddal :P