package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static hu.bgy.pokerapp.enums.Rank.ACE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Getter
public class Hand {
    private final Set<Card> cards;

    private final Map<Rank, Integer> ranks = new HashMap<>(5);
    private final Map<Symbol, Integer> symbols = new HashMap<>(4);
    private final Map<Symbol, List<Rank>> rankBySymbols = new HashMap<>(4);
    //  <multiplications size, pieces>
    private final Map<Integer, Long> rankOccurrences;

    private final int numberOfPairs;
    private final boolean drill;
    private final boolean quad;
    private final boolean straight;
    private final boolean flush;
    private final boolean highest;

    public Hand(final Set<Card> cards) {
        this.cards = cards;
        validate();

        fillRanks();
        fillSymbols();
        fillRankBySymbols();
        rankOccurrences = ranks.values().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        this.numberOfPairs = fillNumberOfPairs();
        this.drill = fillDrill();
        this.quad = fillQuad();
        this.straight = fillStraight();
        this.flush = fillFlush();
        this.highest = fillHighest();
    }

    private void fillRankBySymbols() {
        for (Card card : cards) {
            if (rankBySymbols.containsKey(card.symbol())) {
                final List<Rank> ranks = rankBySymbols.get(card.symbol());
                ranks.add(card.rank());
            } else {
                final List<Rank> ranks = new ArrayList<>();
                ranks.add(card.rank());
                rankBySymbols.put(card.symbol(), ranks);
            }
        }
    }

    private void fillRanks() {
        cards.stream()
                .map(Card::rank)
                .toList()
                .forEach(rank -> fillMap(ranks, rank));
    }

    private void fillSymbols() {
        cards.stream()
                .map(Card::symbol)
                .toList()
                .forEach(symbol -> fillMap(symbols, symbol));
    }

    private <T extends Enum<T>> void fillMap(final Map<T, Integer> map, final T type) {
        if (map.containsKey(type)) {
            map.put(type, map.get(type) + 1);
        } else {
            map.put(type, 1);
        }
    }

    public void validate() {
        if (isEmpty(cards) || cards.size() < 5) {
            throw new IllegalArgumentException();
        }
    }

    private int fillNumberOfPairs() {
        return ranks.values().stream().filter(i -> i == 2).toList().size();
    }

    private boolean fillDrill() {
        return ranks.values()
                .stream()
                .max(Integer::compareTo)
                .orElse(0)
                .equals(3);
    }

    private boolean fillQuad() {
        return ranks.values()
                .stream()
                .max(Integer::compareTo)
                .orElse(0)
                .equals(4);
    }

    private boolean fillStraight() {
        // final List<Integer> values = cards.stream()
        //         .map(Card::rank)
        //         .map(Enum::ordinal)
        //         .sorted()
        //         .toList();
        final List<Rank> ranks = cards.stream().map(Card::rank).collect(Collectors.toSet()).stream().sorted().toList();
        for (int i = 0; i < ranks.size() - 4; i++) {
            if (ranks.get(i).distance(ranks.get(i + 4)) == 4) return true;
        }
        return false;
    }


    private boolean fillFlush() {
        return symbols.values()
                .stream()
                .max(Integer::compareTo)
                .orElse(0) > 4;
    }

    private boolean fillHighest() {
        return cards.stream()
                .map(Card::rank)
                .anyMatch(a -> a == ACE);
    }

    public boolean isRoyalOrStraitFlush(final boolean highestNeeded) {
        return rankBySymbols.values()
                .stream()
                .anyMatch(ranks -> extracted(highestNeeded, ranks));
    }

    private static boolean extracted(boolean highestNeeded, List<Rank> ranks) {
        if (ranks.size() >= 5) {
            ranks.sort(null);
        }
        for (int i = 0; i < ranks.size() - 4; i++) {
            if (ranks.get(i).distance(ranks.get(i + 4)) == 4) {
                if (highestNeeded && ACE.equals(ranks.get(i))) {
                    return true;
                } else if (!highestNeeded) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFullHouse() {
        final int occurrencesHigherThanThree = (int) rankOccurrences.entrySet()
                .stream()
                .filter(integerLongEntry -> integerLongEntry.getKey() > 2)
                .mapToLong(Map.Entry::getValue).sum();
        final int occurrencesEqualTwo = (int) rankOccurrences.entrySet().stream().filter(entry -> entry.getKey() == 2).count();
        return occurrencesHigherThanThree >= 2 || (occurrencesHigherThanThree == 1 && occurrencesEqualTwo >= 1);
    }

    public boolean isTwoPair() {
        return numberOfPairs >= 2;
    }

    public boolean isPair() {
        return numberOfPairs == 1;
    }

    public boolean isNothing() {
        final int occurrencesHigherThanOne = (int) rankOccurrences.entrySet().stream().filter(entry -> entry.getKey() > 1).count();
        return !flush && !straight && occurrencesHigherThanOne == 0;
    }
}

