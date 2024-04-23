package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.Card;
import hu.bgy.pokerapp.models.Hand;
import hu.bgy.pokerapp.models.HandState;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hu.bgy.pokerapp.enums.Rank.ACE;
import static hu.bgy.pokerapp.enums.Value.values;
import static java.util.Arrays.stream;

public class HandValueServiceImpl implements HandValueService {
    public @NonNull Value evaluate(@Nullable final Hand hand) {
        if (hand == null) {
            throw new IllegalArgumentException();
        }
        return stream(values())
                .filter(value -> isMatch(value, hand))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public @NonNull Set<Card> getValuesHand(@NonNull final Hand hand) {
        final Value value = evaluate(hand);
        final Set<TreeSet<Card>> hands = getHandBasedOnValue(value, hand);
        return hands.stream()
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public @NonNull Set<TreeSet<Card>> getHandBasedOnValue(@NonNull final Value value, @NonNull final Hand hand) {
        fillHandState(hand);

        return switch (value) {
            case ROYAL_FLUSH -> getRoyalOrStraightFlush(hand, true);
            case STRAIGHT_FLUSH -> getRoyalOrStraightFlush2(hand, false);
            case POKER -> makePoker(hand);
            case FULL_HOUSE -> getFullHouse(hand);
            case FLUSH -> makeFlush(hand);
            case STRAIGHT -> makeStraight(hand);
            case DRILL -> makeDrill(hand);
            case TWO_PAIRS -> getNPair(hand, 2);
            case PAIR -> getNPair(hand, 1);
            case NOTHING -> getHighestHand(hand);
        };
    }

    private boolean isMatch(@NonNull final Value value, @NonNull final Hand hand) {
        fillHandState(hand);

        return switch (value) {
            case ROYAL_FLUSH, STRAIGHT_FLUSH -> checkRoyalOrStraightFlush(value, hand);
            case POKER -> isPoker(hand);
            case FULL_HOUSE -> isFullHouse(hand);
            case FLUSH -> isFlush(hand);
            case STRAIGHT -> isStraight(hand);
            case DRILL -> isDrill(hand);
            case TWO_PAIRS -> isTwoPair(hand);
            case PAIR -> isPair(hand);
            case NOTHING -> isNothing(hand);
        };
    }

    private void fillHandState(@NonNull final Hand hand) {
        final HandState handState = new HandState(hand.getCards());
        hand.setHandState(handState);
    }

    private boolean checkRoyalOrStraightFlush(@NonNull final Value value, @NonNull final Hand hand) {
        return !value.isStraight() || !value.isFlush() || isRoyalOrStraitFlush(hand, value.isHighest());
    }

    public boolean isRoyalOrStraitFlush(@NonNull final Hand hand, final boolean highestNeeded) {
        return hand.getHandState().getRankBySymbols().values()
                .stream()
                .anyMatch(ranks -> hasRoyalOrStraightFlush(highestNeeded, ranks));
    }

    private static boolean hasRoyalOrStraightFlush(final boolean highestNeeded, final TreeSet<Rank> ranks) {
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

    private boolean isPoker(@NonNull final Hand hand) {
        return hand.getHandState().getRanks().values()
                .stream()
                .max(Integer::compareTo)
                .orElse(0)
                .equals(4);
    }

    public boolean isFullHouse(@NonNull final Hand hand) {
        final Map<Integer, Long> rankOccurrences = hand.getHandState().getRankOccurrences();
        final int occurrencesHigherThanThree = (int) rankOccurrences.entrySet()
                .stream()
                .filter(integerLongEntry -> integerLongEntry.getKey() > 2)
                .mapToLong(Map.Entry::getValue).sum();
        final int occurrencesEqualTwo = (int) rankOccurrences.entrySet().stream().filter(entry -> entry.getKey() == 2).count();
        return occurrencesHigherThanThree >= 2 || (occurrencesHigherThanThree == 1 && occurrencesEqualTwo >= 1);
    }

    private boolean isFlush(@NonNull final Hand hand) {
        return hand.getHandState().getSymbols().values()
                .stream()
                .anyMatch(i -> i > 4);
    }


    private boolean isStraight(@NonNull final Hand hand) {
        final List<Rank> ranks = hand.getCards()
                .stream()
                .map(Card::rank)
                .collect(Collectors.toSet())
                .stream()
                .sorted()
                .toList();
        return IntStream.range(0, ranks.size() - 4)
                .anyMatch(i -> ranks.get(i).distance(ranks.get(i + 4)) == 4);
    }

    private boolean isDrill(@NonNull final Hand hand) {
        return hand.getHandState().getRanks().values()
                .stream()
                .max(Integer::compareTo)
                .orElse(0)
                .equals(3);
    }

    public boolean isTwoPair(@NonNull final Hand hand) {
        return getNumberOfPairs(hand) >= 2;
    }


    public boolean isPair(@NonNull final Hand hand) {
        return getNumberOfPairs(hand) == 1;
    }

    private int getNumberOfPairs(@NonNull final Hand hand) {
        return hand.getHandState().getRanks().values().stream()
                .filter(i -> i == 2)
                .toList()
                .size();
    }

    public boolean isNothing(@NonNull final Hand hand) {
        final Map<Integer, Long> rankOccurrences = hand.getHandState().getRankOccurrences();
        final int occurrencesHigherThanOne = (int) rankOccurrences.entrySet().stream()
                .filter(entry -> entry.getKey() > 1)
                .count();
        return !isFlush(hand) && !isStraight(hand) && occurrencesHigherThanOne == 0;
    }

    public Set<TreeSet<Card>> getRoyalOrStraightFlush(@NonNull final Hand hand, final boolean highest) {
        final Set<TreeSet<Card>> highestHands = hand.getHandState().getRankBySymbols().entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= 5)
                .map(entry -> findStraight(hand, entry.getKey(), entry.getValue()))
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

    public Set<TreeSet<Card>> getRoyalOrStraightFlush2(@NonNull final Hand hand, final boolean highest) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        for (Map.Entry<Symbol, TreeSet<Rank>> entry : hand.getHandState().getRankBySymbols().entrySet()) {
            if (entry.getValue().size() < 5) {
                continue;
            }
            final TreeSet<Card> highestHand = findStraight(hand, entry.getKey(), entry.getValue());
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
            @NonNull final Hand hand,
            @NonNull final Symbol symbol,
            @NonNull final TreeSet<Rank> ranks) {
        final List<Rank> rankList = ranks.stream().toList();
        for (int i = 0; i < rankList.size() - 4; i++) {
            if (rankList.get(i).distance(rankList.get(i + 4)) == 4) {
                return collectCards(hand, symbol, rankList.subList(i, i + 5));
            }
        }
        return new TreeSet<>();
    }

    private @NonNull TreeSet<Card> collectCards(
            @NonNull final Hand hand,
            @NonNull final Symbol symbol,
            @NonNull final List<Rank> ranks) {
        return ranks.stream()
                .map(rank -> getCard(hand, symbol, rank))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private @NonNull Card getCard(
            @NonNull final Hand hand,
            @NonNull final Symbol symbol,
            @NonNull final Rank rank) {
        return hand.getCards().stream()
                .filter(card -> card.rank() == rank && card.symbol() == symbol)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public @NonNull Set<TreeSet<Card>> getFullHouse(@NonNull final Hand hand) {
        final Rank drill = getRankThatOccursAtLeast(hand, 3).getFirst();
        final TreeSet<Rank> pairs = getRankThatOccursAtLeast(hand, 2);
        pairs.remove(drill);
        final Map<Rank, Integer> map = Map.of(
                drill, 3,
                pairs.getFirst(), 2);

        return Set.of(getCardsForList(hand, map));
    }

    public @NonNull Set<TreeSet<Card>> makeDrill(@NonNull final Hand hand) {
        final Rank forDrill = getRankThatOccursAtLeast(hand, 3).getFirst();
        final Map<Rank, Integer> map = Map.of(forDrill, 3);

        return Set.of(getCardsForList(hand, map));
    }

    private @NonNull TreeSet<Rank> getRankThatOccursAtLeast(@NonNull final Hand hand, final int occurrence) {
        return hand.getHandState().getRanks()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= occurrence)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private @NonNull TreeSet<Card> getCardsForList(
            @NonNull final Hand hand,
            @NonNull final Map<Rank, Integer> map) {
        final TreeSet<Card> result = new TreeSet<>();
        for (Map.Entry<Rank, Integer> entry : map.entrySet()) {
            Set<Card> collections = hand.getCards().stream()
                    .filter(card -> card.rank().equals(entry.getKey()))
                    .limit(entry.getValue())
                    .collect(Collectors.toSet());
            result.addAll(collections);
        }

        return fillHighCards(hand, result);
    }

    public @NonNull Set<TreeSet<Card>> makePoker(@NonNull final Hand hand) {
        final Rank poker = getRankThatOccursAtLeast(hand, 4).first();
        final Map<Rank, Integer> map = Map.of(poker, 4);

        return Set.of(getCardsForList(hand, map));
    }

    private TreeSet<Card> fillHighCards(
            @NonNull final Hand hand,
            @NonNull final TreeSet<Card> setCards) {
        TreeSet<Card> treeSet = hand.getCards().stream()
                .filter(card -> !setCards.contains(card))
                .collect(Collectors
                        .toCollection(TreeSet::new));
        while (setCards.size() < 5) {
            setCards.add(treeSet.getFirst());
            treeSet.remove(treeSet.getFirst());
        }
        return setCards;
    }


    public @NonNull Set<TreeSet<Card>> makeFlush(@NonNull final Hand hand) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        hand.getHandState().getRankBySymbols()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= 5)
                .forEach(entry -> {
                    final List<Rank> rankList = entry
                            .getValue()
                            .stream()
                            .toList();
                    final TreeSet<Card> highestHand = collectCards(hand, entry.getKey(), rankList.subList(0, 5));
                    handleHighestHand(highestHand, highestHands, false);
                });
        return highestHands;
    }

    public @NonNull Set<TreeSet<Card>> getNPair(@NonNull final Hand hand, final int numberOfPairs) {
        final List<Rank> pairs = getRankThatOccursAtLeast(hand, 2)
                .stream()
                .limit(numberOfPairs)
                .toList();
        final Map<Rank, Integer> map = pairs.stream()
                .collect(Collectors.toMap(rank -> rank, rank -> 2, (a, b) -> b));

        return Set.of(getCardsForList(hand, map));
    }

    public Set<TreeSet<Card>> getHighestHand(@NonNull final Hand hand) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        highestHands.add(new TreeSet<>(new HashSet<>(hand.getCards()).stream().sorted().toList().subList(0, 5)));
        return highestHands;
    }

    public @NonNull Set<TreeSet<Card>> makeStraight(@NonNull final Hand hand) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        final List<Card> line = new HashSet<>(hand.getCards()).stream().sorted().toList();
        IntStream
                .range(0, line.size() - 4)
                .filter(i -> line.get(i).rank().distance(line.get(i + 4).rank()) == 4)
                .mapToObj(i -> new TreeSet<>(line.subList(i, i + 5)))
                .forEach(highestHand -> handleHighestHand(highestHand, highestHands, false));
        return highestHands;
    }

}
