package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "player_states")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerState {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne()
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Enumerated(EnumType.STRING)
    @Column(name = "in_game_state", nullable = false)
    private InGameState inGameState;

    @Enumerated(EnumType.STRING)
    @Column(name = "round_role", nullable = false)
    private RoundRole roundRole;
}
