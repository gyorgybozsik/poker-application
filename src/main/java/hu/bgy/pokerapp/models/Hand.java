package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hu.bgy.pokerapp.enums.Rank.ACE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Getter
public class Hand {
    private final Set<Card> cards;

    private final Map<Rank, Integer> ranks = new HashMap<>(5);
    private final Map<Symbol, Integer> symbols = new HashMap<>(4);
    private final Map<Symbol, TreeSet<Rank>> rankBySymbols = new HashMap<>(4);
    //  <multiplications size, pieces>
    private final Map<Integer, Long> rankOccurrences;

    private final int numberOfPairs;
    private final boolean drill;
    private final boolean poker;
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
        this.poker = fillQuad();
        this.straight = fillStraight();
        this.flush = fillFlush();
        this.highest = fillHighest();
    }

    private void fillRankBySymbols() {
        for (Card card : cards) {
            if (rankBySymbols.containsKey(card.symbol())) {
                final TreeSet<Rank> ranks = rankBySymbols.get(card.symbol());
                ranks.add(card.rank());
            } else {
                final TreeSet<Rank> ranks = new TreeSet<>();
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
        return IntStream.range(0, ranks.size() - 4).anyMatch(i -> ranks.get(i).distance(ranks.get(i + 4)) == 4);
    }


    private boolean fillFlush() {
        boolean seen = false;
        Integer best = null;
        for (Iterator<Integer> iterator = symbols.values().iterator(); iterator.hasNext(); ) {
            Integer i = iterator.next();
            if (!seen || i.compareTo(best) > 0) {
                seen = true;
                best = i;
            }
        }
        return (seen ? best : 0) > 4;
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

    private static boolean extracted(final boolean highestNeeded, final TreeSet<Rank> ranks) {
        final List<Rank> rankList = ranks.stream().toList();
        for (int i = 0; i < ranks.size() - 4; i++) {
            if (rankList.get(i).distance(rankList.get(i + 4)) == 4) {
                if (highestNeeded && ACE.equals(rankList.get(i))) {
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

    public Set<TreeSet<Card>> getRoyalOrStraightFlush(final boolean highest) {
        final Set<TreeSet<Card>> highestHands = rankBySymbols.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= 5)
                .map(entry -> findStraight(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());

        final Rank highestRank = highest ?
                Rank.getHighest() :
                highestHands.stream()
                        .map(SortedSet::getFirst)
                        .map(Card::rank)
                        .max(Comparator.naturalOrder())
                        .orElseThrow(IllegalArgumentException::new);

        return highestHands.stream()
                .filter(set -> !set.isEmpty())
                .filter(cards -> cards.getFirst().isEqual(highestRank))
                .collect(Collectors.toSet());
    }

    public Set<TreeSet<Card>> getRoyalOrStraightFlush2(final boolean highest) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        for (Map.Entry<Symbol, TreeSet<Rank>> entry : rankBySymbols.entrySet()) {
            if (entry.getValue().size() < 5) {
                continue;
            }
            final TreeSet<Card> highestHand = findStraight(entry.getKey(), entry.getValue());
            handleHighestHand(highestHand, highestHands, highest);
        }
        return highestHands;
    }

    private void handleHighestHand(
            @NonNull final TreeSet<Card> highestHand, // amit most találunk sínek alapják kéz
            @NonNull final Set<TreeSet<Card>> highestHands, // ez amit előző iterációkba találtuunk kezek listája
            final boolean highest) {
        if (highestHand.isEmpty()) { // ha a mostani lisrta ures akkor nem csinálunk semmit. mert nem találtunk sort
            return;
        }
        if (highestHands.isEmpty()) { // ha az előző találatok litája üres
            if (highest && highestHand.getFirst().isHighest()) { // itt kezeljük el az első találatot Royal Flush esetén
                highestHands.add(highestHand);
            } else if (!highest) { // itt kezeljük el az első találatot Straight Flush esetén
                highestHands.add(highestHand);
            }
            return;
        }

        if (highest && highestHand.getFirst().isHighest()) {
            highestHands.add(highestHand);
            return;
        }

        if (!highest) {
            final Rank previousHighest = highestHands
                    .stream()
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new)
                    .getFirst()
                    .rank();
            final Rank currentHighest = highestHand
                    .getFirst()
                    .rank();
            if (currentHighest.isHigher(previousHighest)) {
                highestHands.clear();
                highestHands.add(highestHand);
            } else if (!previousHighest.isHigher(currentHighest)) {
                highestHands.add(highestHand);
            }
        }
    }

    private TreeSet<Card> findStraight(
            @NonNull final Symbol symbol,
            @NonNull final TreeSet<Rank> ranks) {
        final List<Rank> rankList = ranks.stream().toList();
        for (int i = 0; i < rankList.size() - 4; i++) {
            if (rankList.get(i).distance(rankList.get(i + 4)) == 4) {
                return collectCards(symbol, rankList.subList(i, i + 5));
            }
        }
        return new TreeSet<>();
    }

    private TreeSet<Card> makePokerHand(
            @NonNull final Rank rank) {
        return cards
                .stream()
                .filter(card -> card.isEqual(rank))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private @NonNull TreeSet<Card> collectCards(
            @NonNull final Symbol symbol,
            @NonNull final List<Rank> ranks) {
        return ranks.stream()
                .map(rank -> getCard(symbol, rank))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private @NonNull Card getCard(
            @NonNull final Symbol symbol,
            @NonNull final Rank rank) {
        return cards.stream()
                .filter(card -> card.rank() == rank && card.symbol() == symbol)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public Set<TreeSet<Card>> getFullHouse(final boolean highest) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        TreeSet<Rank> drill = ranks
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
        TreeSet<Rank> pairs = ranks
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 3 || entry.getValue() == 2)
                .filter(entry -> !entry.getKey().equals(drill.getFirst()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
        if (highest) highestHands.add(getCardsForList(drill.getFirst(), pairs.getFirst(), highest));
        if (!highest) highestHands.add(getCardsForList(drill.getFirst(), null, highest));
        return highestHands;
    }


    //     final Set<TreeSet<Card>> highestHands = new HashSet<>();
    //     Rank drill = null, pair = null;
    //     boolean seenDrill = false, seenPair = false;
    //     for (Iterator<Map.Entry<Rank, Integer>> iterator = ranks.entrySet().iterator(); iterator.hasNext(); ) {
    //         Map.Entry<Rank, Integer> entry = iterator.next();
    //         if (entry.getValue() == 3 || entry.getValue() == 2) {
    //             if (entry.getValue() == 3 && (!seenDrill || !drill.isHigher(entry.getKey()))) {
    //                 seenDrill = true;
    //                 drill = entry.getKey();
    //             } else if (!seenPair || !pair.isHigher(entry.getKey())) {
    //                 seenPair = true;
    //                 pair = entry.getKey();
    //             }
    //         }
    //     }
    //     TreeSet<Card> result = getCardsForList(drill, pair, highest);
    //     highestHands.add(result);
    //     return highestHands;
    // }

    private TreeSet<Card> getCardsForList(
            @NonNull Rank rank1,
            Rank rank2,
            boolean highest) {
        List<Card> few = new ArrayList<>();
        int pairCount = 0;
        for (Card card : cards) {
            if (card.isEqual(rank1)) few.add(card);
            if (highest && card.isEqual(rank2) && pairCount < 2) {
                few.add(card);
                pairCount++;
            }
        }
        TreeSet<Card> result = new TreeSet<>(few);
        return fillHighCards(result);
    }


    public Set<TreeSet<Card>> makePoker() {
        Set<TreeSet<Card>> highestHands = new HashSet<>();
        ranks.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 4)
                .map(entry -> makePokerHand(entry.getKey()))
                .map(this::fillHighCards)
                .forEach(highestHand -> handleHighestHand(highestHand, highestHands, false));
        return highestHands;
    }

    private TreeSet<Card> fillHighCards(TreeSet<Card> setCards) {
        TreeSet<Card> treeSet = cards.stream()
                .filter(card -> !setCards.contains(card))
                .collect(Collectors
                        .toCollection(TreeSet::new));
        while (setCards.size() < 5) {
            setCards.add(treeSet.getFirst());
            treeSet.remove(treeSet.getFirst());
        }
        return setCards;
    }


    public Set<TreeSet<Card>> makeFlush() {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        rankBySymbols
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= 5)
                .forEach(entry -> {
                    final List<Rank> rankList = entry
                            .getValue()
                            .stream()
                            .toList();
                    final TreeSet<Card> highestHand = collectCards(entry.getKey(), rankList.subList(0, 5));
                    handleHighestHand(highestHand, highestHands, false);
                });
        return highestHands;
    }

    public Set<TreeSet<Card>> getPair(final boolean highest) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        List<Rank> pairs = ranks
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 2)
                .map(Map.Entry::getKey)
                .distinct().sorted()
                .collect(Collectors.toList());
        highestHands.add(getCardsForList(pairs.get(0), pairs.get(1), highest));
        return highestHands;
    }

    public Set<TreeSet<Card>> getHighestHand() {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        //final List<Card> line= new HashSet<>(cards).stream().sorted().toList().subList(0,5);
        //;
        //TreeSet<Card> fiveHigh = new TreeSet<>(line);
        //highestHands.add(fiveHigh);
        highestHands.add(new TreeSet<>(new HashSet<>(cards).stream().sorted().toList().subList(0, 5)));
        return highestHands;
    }

    public Set<TreeSet<Card>> makeStraight() {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        final List<Card> line = new HashSet<>(cards).stream().sorted().toList();
        IntStream
                .range(0, line.size() - 4)
                .filter(i -> line.get(i).rank().distance(line.get(i + 4).rank()) == 4)
                .mapToObj(i -> new TreeSet<>(line.subList(i, i + 5)))
                .forEach(highestHand -> handleHighestHand(highestHand, highestHands, false));
        return highestHands;
    }
}
