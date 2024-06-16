package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.PokerType;
import hu.bgy.pokerapp.enums.RoundRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

import static hu.bgy.pokerapp.enums.RoundRole.SPEAKER_1;


@Data
@Entity
@jakarta.persistence.Table(name = "tables")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    // @SequenceGenerator(name = "t_seq_g", sequenceName = "tables_id_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "poker_type", nullable = false)
    private PokerType pokerType;

    @Column(name = "small_blind", nullable = false)
    private BigDecimal smallBlind;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<Player> seats;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaker", nullable = false)
    private RoundRole speaker;


    public Table(@NonNull final PokerType pokerType,
                 @NonNull final BigDecimal smallBlind) {
        this.pokerType = pokerType;
        this.smallBlind = smallBlind;
        speaker = SPEAKER_1;
    }
}
//todo long ID --> uuid
// adatbázis script serial heylet uuid,
// UUid generálás megvalósítása
// postgesql uuid generálás<-keresés

