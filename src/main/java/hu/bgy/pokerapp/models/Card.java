package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import lombok.NonNull;

public record Card(Symbol symbol,
                   Rank rank) implements Comparable<Card>  {
    public boolean isHighest() {
        return rank.isHighest();
    }

    public boolean isEqual(@NonNull final Rank highestRank) {
        return rank.equals(highestRank);
    }

    @Override
    public int compareTo(Card o) {
        return this.rank.ordinal() - o.rank.ordinal();
    }
}
