package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "players")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    //  @SequenceGenerator(name = "p_seq_g", sequenceName = "players_id_seq")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id", referencedColumnName = "id", nullable = false)
    private Balance balance;

    @ToString.Exclude
    @OneToOne(mappedBy = "player")
    private Hand hand;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    private PlayerState state;

    @ToString.Exclude
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
        state = new PlayerState(this, roundRole);
    }


    public boolean isSpeakable(@NonNull final RoundRole role) {
        return state.isActiveRoundRole(role) && balance.hasCash();
    }

    public void fold() {
        this.state.setInGameState(InGameState.SIT_OUT);
    }

    public boolean isActive() {
        return InGameState.ACTIVE.equals(state.getInGameState());
    }


    public void bet(@NonNull final BigDecimal betAmount) {
        balance.bet(betAmount);
    }
}
