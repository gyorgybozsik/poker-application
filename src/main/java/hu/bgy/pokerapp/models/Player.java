package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
  //  @SequenceGenerator(name = "p_seq_g", sequenceName = "players_id_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id", referencedColumnName = "id", nullable = false)
    private Balance balance;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    private PlayerState state;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private hu.bgy.pokerapp.models.Table table;

    public Player(
            @NonNull final String name,
            @NonNull final BigDecimal cash,
            @NonNull final RoundRole roundRole) {
        this.name = name;
        this.balance = new Balance(cash);
        balance.setPlayer(this);
        state = new PlayerState(this,roundRole);
    }

    public boolean isActive() {
        return InGameState.ACTIVE.equals(state);
    }

    public void bet(@NonNull final BigDecimal betAmount) {
        balance.bet(betAmount);
    }
}
