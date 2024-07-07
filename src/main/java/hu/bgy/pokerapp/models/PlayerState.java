package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Table(name = "player_states")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerState {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    //@SequenceGenerator(name = "ps_seq_g", sequenceName = "player_states_id_seq")
    private Long id;

    @OneToOne()
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;

    // @Column(name = "table_id", nullable = false)
    // private Long tableId;

    @Enumerated(EnumType.STRING)
    @Column(name = "in_game_state", nullable = false)
    private InGameState inGameState;

    @Enumerated(EnumType.STRING)
    @Column(name = "round_role", nullable = false)
    private RoundRole roundRole;

    public PlayerState(Player player, RoundRole roundRole) {
        inGameState = InGameState.ACTIVE;
        this.roundRole = roundRole;
        this.player = player;
    }

    public boolean isActiveRoundRole(@NonNull RoundRole roundRole) {
        return inGameState == InGameState.ACTIVE && roundRole == this.roundRole;
    }
}
