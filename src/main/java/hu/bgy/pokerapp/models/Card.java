package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import lombok.NonNull;

import java.util.Comparator;

public record Card(Symbol symbol,
                   Rank rank) implements Comparable<Card> {
    public boolean isHighest() {
        return rank.isHighest();
    }

    public boolean isEqual(@NonNull final Rank highestRank) {
        return rank.equals(highestRank);
    }

    @Override
    public int compareTo(Card o) {
        //final int diff =  this.rank.ordinal() - o.rank.ordinal();
        if (this.rank.compareTo(o.rank) == 0) {
            return this.symbol.compareTo(o.symbol);
        } else {
            return this.rank.compareTo(o.rank);
        }

        //return Comparator.comparing(rank.ordinal());
    }
    //  @Override
    //  public int compareTo(Card o) {
    //      return this.rank.ordinal() - o.rank.ordinal();
    //  }

    final static Comparator<Card> cardComparator = Comparator.comparing(Card::rank).thenComparing(Card::symbol);
}
