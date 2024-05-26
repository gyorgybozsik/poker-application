package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import jakarta.persistence.*;

public class PlayerState {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne()
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
