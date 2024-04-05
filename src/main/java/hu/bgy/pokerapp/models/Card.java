package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import lombok.NonNull;

public record Card(Symbol symbol,
                   Rank rank) implements Comparable<Card> {

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
