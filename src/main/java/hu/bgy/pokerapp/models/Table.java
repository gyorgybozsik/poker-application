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


@Data
@Entity
@jakarta.persistence.Table(name = "tables")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "poker_type", nullable = false)
    private PokerType pokerType;

    @Column (name = "small_blind",nullable = false)
    private BigDecimal smallBlind;

    @OneToMany (mappedBy = "table")
    private List<Player> seats;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaker", nullable = false)
    private RoundRole speaker;


    public Table(@NonNull final PokerType pokerType,
                 @NonNull final BigDecimal smallBlind) {
        this.pokerType = pokerType;
        this.smallBlind = smallBlind;
    }


}
