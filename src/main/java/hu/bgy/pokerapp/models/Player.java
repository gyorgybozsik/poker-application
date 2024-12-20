package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@Entity
@Table(name = "players")
@NoArgsConstructor
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

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
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
        hand = new Hand();
        hand.setPlayer(this);
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public boolean hasNoBet() {
        return BigDecimal.ZERO.compareTo(this.getBalance().getBet()) == 0;
    }

    public boolean isThisHigherHandOrEqual(@NonNull Player player,
                                           boolean isItAnEqualSerial) {
        List<Card> actualBest = new ArrayList<>(this.getHand().getCards());
        List<Card> challengingPlayer = new ArrayList<>(player.getHand().getCards());

        for (int i = 0; i < 5; i++) {
            if (actualBest.get(i).getRank().isHigher(challengingPlayer.get(i).getRank())) return true;
            else if (actualBest.get(i).getRank().equals(challengingPlayer.get(i).getRank())) continue;
            if (isItAnEqualSerial && i == 4) return true;
            break;
        }
        return false;
    }
}
