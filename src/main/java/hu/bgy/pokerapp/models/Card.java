package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "cards")
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Card implements Comparable<Card> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "symbol", nullable = false)
    private Symbol symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false)
    private Rank rank;

    public boolean isHighest() {
        return rank.isHighest();
    }

    public boolean isEqual(@NonNull final Rank rank) {
        return this.rank.equals(rank);
    }

    @Override
    public int compareTo(@NonNull final Card o) {
        if (this.rank.compareTo(o.rank) == 0) {
            return this.symbol.compareTo(o.symbol);
        } else {
            return this.rank.compareTo(o.rank);
        }
    }
}
