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
import java.util.UUID;

import static hu.bgy.pokerapp.enums.RoundRole.*;


@Data
@Entity
@jakarta.persistence.Table(name = "tables")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // @SequenceGenerator(name = "t_seq_g", sequenceName = "tables_id_seq")
    private UUID id;

    @Column(name = "round", nullable = false)
    private int round;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "after_last", nullable = false)
    private RoundRole afterLast;

    public void setSeats(List<Player> seats) {
        this.seats = seats;
        if (seats.size() == 2) {
            speaker = RoundRole.SMALL_BLIND;
            afterLast = SMALL_BLIND;
        } else {
            speaker = SPEAKER_1;
            afterLast = SPEAKER_1;
        }

    }

    public Table(@NonNull final PokerType pokerType,
                 @NonNull final BigDecimal smallBlind) {
        this.pokerType = pokerType;
        this.smallBlind = smallBlind;
    }
}
//todo long ID --> uuid
// adatbázis script serial heylet uuid,
// UUid generálás megvalósítása
// postgesql uuid generálás<-keresés

