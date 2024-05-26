package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.PlayerState;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "players")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id", referencedColumnName = "id", nullable = false)
    private Balance balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private PlayerState state;

    public Player(
            @NonNull final String name,
            @NonNull final BigDecimal cash) {
        this.name = name;
        this.balance = new Balance(cash);

        state = PlayerState.ACTIVE;
    }

    public boolean isActive() {
        return PlayerState.ACTIVE.equals(state);
    }

    public void bet(@NonNull final BigDecimal betAmount) {
        balance.bet(betAmount);
    }
}
