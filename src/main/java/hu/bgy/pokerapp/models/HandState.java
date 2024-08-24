package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class HandState {

    private final Map<Rank, Integer> ranks;
    private final Map<Symbol, Integer> symbols;
    private final Map<Symbol, TreeSet<Rank>> rankBySymbols;
    private final Map<Integer, Long> rankOccurrences;

    public HandState(@NonNull final TreeSet<Card> cards) {
        this.ranks = fillRanks(cards);
        this.symbols = fillSymbols(cards);
        this.rankBySymbols = fillRankBySymbols(cards);
        this.rankOccurrences = fillRankOccurrences(ranks);
    }

    private @NonNull Map<Rank, Integer> fillRanks(@NonNull final TreeSet<Card> cards) {
        final Map<Rank, Integer> ranks = new HashMap<>();
        cards.stream()
                .map(Card::getRank)
                .toList()
                .forEach(rank -> fillMap(ranks, rank));
        return ranks;
    }


    private Map<Symbol, Integer> fillSymbols(@NonNull final TreeSet<Card> cards) {
        final Map<Symbol, Integer> symbols = new HashMap<>();
        cards.stream()
                .map(Card::getSymbol)
                .toList()
                .forEach(symbol -> fillMap(symbols, symbol));
        return symbols;
    }

    private <T extends Enum<T>> void fillMap(@NonNull final Map<T, Integer> map, @NonNull final T type) {
        if (map.containsKey(type)) {
            map.put(type, map.get(type) + 1);
        } else {
            map.put(type, 1);
        }
    }

    private Map<Symbol, TreeSet<Rank>> fillRankBySymbols(@NonNull final TreeSet<Card> cards) {
        final Map<Symbol, TreeSet<Rank>> rankBySymbols = new HashMap<>();
        for (Card card : cards) {
            if (rankBySymbols.containsKey(card.getSymbol())) {
                final TreeSet<Rank> ranks = rankBySymbols.get(card.getSymbol());
                ranks.add(card.getRank());
            } else {
                final TreeSet<Rank> ranks = new TreeSet<>();
                ranks.add(card.getRank());
                rankBySymbols.put(card.getSymbol(), ranks);
            }
        }
        return rankBySymbols;
    }

    private Map<Integer, Long> fillRankOccurrences(@NonNull final Map<Rank, Integer> ranks) {
        return ranks.values().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
