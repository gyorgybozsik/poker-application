package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;
import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.models.*;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hu.bgy.pokerapp.enums.Rank.ACE;
import static hu.bgy.pokerapp.enums.Value.*;
import static hu.bgy.pokerapp.enums.Value.PAIR;
import static java.util.Arrays.stream;

@Service
public class HandValueServiceImpl implements HandValueService {
    public @NonNull Value evaluate(@Nullable final Hand hand) {
        if (hand == null) {
            throw new IllegalArgumentException();
        }
        fillHandState(hand);
        Value value = stream(values())
                .filter(v -> isMatch(v, hand))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        hand.setHandScore(value);

        return value;
    }

    @Override
    public @NonNull Set<Card> getValuesHand(@NonNull final Hand hand) {
        final Value value = evaluate(hand);
        final Set<TreeSet<Card>> hands = getHandBasedOnValue(value, hand);
        return hands.stream()
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static List<List<Player>> highSerial(@NonNull List<Player> playersWithSameValue) {
        List<List<Player>> valuesInOrder = new ArrayList<>();
        for (Player player : playersWithSameValue) {
            if (valuesInOrder.isEmpty()) {
                List<Player> serial = new ArrayList<>(Collections.singleton(player));
            } else {
                for (int i = 0; i < valuesInOrder.size(); i++) {

                    if (valuesInOrder.get(i).getFirst().isThisHigherHandOrEqual(player, false)) {
                        List<Player> newSerial = new ArrayList<>(Collections.singleton(player));
                        valuesInOrder.add(i, newSerial);
                        break;
                    } else if (valuesInOrder.get(i).getFirst().isThisHigherHandOrEqual(player, true)) {
                        valuesInOrder.get(i).add(player);
                        break;
                    } else if (i == valuesInOrder.size() - 1) {
                        List<Player> newSerial = new ArrayList<>(Collections.singleton(player));
                        valuesInOrder.add(newSerial);
                    }
                }
            }
        }
        return valuesInOrder;
    }

    private boolean combinationValue(@NonNull Value valueOfInterest) {
        return valueOfInterest == POKER ||
                valueOfInterest == FULL_HOUSE ||
                valueOfInterest == DRILL ||
                valueOfInterest == TWO_PAIRS ||
                valueOfInterest == PAIR;
    }

    private boolean isItHigherHand(@NonNull Player playerFromOrderedList,
                                   @NonNull Map<Player,TreeMap<Rank,Integer>> details,
                                   @NonNull  Player challengerPlayer) {
        TreeMap<Rank, Integer> playerFromOrderedListRanks = details.get(playerFromOrderedList);
        TreeMap<Rank, Integer> challengerPayersRanks = details.get(challengerPlayer);
        if (combinationValue(playerFromOrderedList.getHand().getHandScore())) {
            Value handValue = playerFromOrderedList
                    .getHand()
                    .getHandScore();
            Integer key = getCompinationCardSize(handValue);
            Rank fromListRank = getRankFromMap(playerFromOrderedListRanks, key);
            Rank challengerRank = getRankFromMap(challengerPayersRanks, key);
            if (challengerRank.isHigher(fromListRank)) return true;
            if (challengerRank.equals(fromListRank) && handValue == FULL_HOUSE || handValue == TWO_PAIRS) {
                playerFromOrderedListRanks.remove(fromListRank, key);
                challengerPayersRanks.remove(challengerRank, key);
                key = 2;
                fromListRank = getRankFromMap(playerFromOrderedListRanks, key);
                challengerRank = getRankFromMap(challengerPayersRanks, key);
                if (challengerRank.isHigher(fromListRank)) return true;
                if (handValue == FULL_HOUSE && challengerRank.equals(fromListRank)) return false;
                playerFromOrderedListRanks.remove(fromListRank, key);
                challengerPayersRanks.remove(challengerRank, key);
            }
        }
        if (playerFromOrderedListRanks.equals(challengerPayersRanks)) return false;
        int round = playerFromOrderedListRanks.size();
        for (int i = 0; i < round; i++) {
            Rank playerFromList = playerFromOrderedListRanks.firstEntry().getKey();
            Rank playerWhoIsChallenge = challengerPayersRanks.firstEntry().getKey();
            if (playerWhoIsChallenge.isHigher(playerFromList))
                return true;
            playerFromOrderedListRanks.pollFirstEntry();
            challengerPayersRanks.pollFirstEntry();
        }
        return false;
    }

   @NonNull
    private static Rank getRankFromMap(Map<Rank, Integer> playerFromOrderedListRanks, Integer key) {
        return playerFromOrderedListRanks.entrySet()
                .stream()
                .filter(rankEntry -> rankEntry.getValue().equals(key))
                .map(Map.Entry::getKey).findFirst().orElseThrow(IllegalStateException::new);
    }

    private List<List<Player>> highCombination(List<Player> players) {
        List<Player> winner = new ArrayList<>();
        Value handValue = players
                .getFirst()
                .getHand()
                .getHandScore();
        Integer key = getCompinationCardSize(handValue);

        Map<Player, TreeMap<Rank, Integer>> details = new TreeMap<>();
        players.forEach(player -> {
            TreeMap<Rank, Integer> cards = fillRanks(player.getHand().getCards());
            details.put(player, cards);
        });
//todo itt hagytam félbe-----

        //       for (Map.Entry<Player, TreeMap<Rank, Integer>> playerEntry : details.entrySet()) {
//
        //           for (Map.Entry<Rank, Integer> rankEntry : playerEntry.getValue().entrySet()) {
        //               if (rankEntry.getValue() == key && (bestRank == null || rankEntry.getKey().isHigher(bestRank))) {
        //                   bestRank = rankEntry.getKey();
        //               }
        //               bestRank = rankEntry.getKey();
        //               // winner = entry.getKey();
        //           }
        //       }
               return Collections.singletonList(winner);
    }

    @org.jetbrains.annotations.Nullable
    private static Integer getCompinationCardSize(Value handValue) {
        Integer key = null;
        switch (handValue) {
            case POKER -> key = 4;
            case FULL_HOUSE, DRILL -> key = 3;
            case PAIR, TWO_PAIRS -> key = 2;
        }
        return key;
    }

    private List<Player> highCard(List<Player> players, Table table) {
        List<Player> winner = new ArrayList<>();
        for (Player player : players) {
            if (player.getHand().getCards().getFirst().getRank().isHigher(winner.getFirst().getHand().getCards().getFirst().getRank())) {
                winner.clear();
                winner.add(player);
            }
            if (player.getHand().getCards().getFirst().getRank().equals(winner.getFirst().getHand().getCards().getFirst().getRank()))
                winner.add(player);
        }
        return winner;
    }

    public @NonNull TreeMap<Rank, Integer> fillRanks(@NonNull final TreeSet<Card> cards) {
        final TreeMap<Rank, Integer> ranks = new TreeMap<>();
        cards.stream()
                .map(Card::getRank)
                .toList()
                .forEach(rank -> fillMap(ranks, rank));
        return ranks;
    }

    private <T extends Enum<T>> void fillMap(@NonNull final Map<T, Integer> map, @NonNull final T type) {
        if (map.containsKey(type)) {
            map.put(type, map.get(type) + 1);
        } else {
            map.put(type, 1);
        }
    }


    @Override
    public List<List<Player>> orderWithHighestCard(Value value, List<Player> playersWithSameValue) {
        List<List<Player>> valuesInOrder = new ArrayList<>();

        Map<Player, TreeMap<Rank, Integer>> details = new HashMap<>();
        playersWithSameValue.forEach(player -> {
            TreeMap<Rank, Integer> cards = fillRanks(player.getHand().getCards());
            details.put(player, cards);
        });

        for (Player challengerPlayer : playersWithSameValue) {
            if (valuesInOrder.isEmpty()) {
                List<Player> newSerial = new ArrayList<>(Collections.singleton(challengerPlayer));
                valuesInOrder.add(newSerial);
            } else {
                int rounds = valuesInOrder.size();
                for (int i = 0; i < rounds; i++) {
                    Player playerFromOrderedList = valuesInOrder.get(i).getFirst();
                    if (details.get(playerFromOrderedList).equals(details.get(challengerPlayer))) {
                        valuesInOrder.get(i).add(challengerPlayer);
                        break;
                    }
                    if (isItHigherHand(playerFromOrderedList, details, challengerPlayer)) {
                        List<Player> newSerial = new ArrayList<>(Collections.singleton(challengerPlayer));
                        valuesInOrder.add(i, newSerial);
                        break;
                    }
                    if (i == valuesInOrder.size() - 1) {
                        List<Player> newSerial = new ArrayList<>(Collections.singleton(challengerPlayer));
                        valuesInOrder.add(newSerial);
                        continue;
                    }
                }
            }
        }
        return valuesInOrder;
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

    private void fillHandState(@NonNull final Hand hand) {//todo : Majd table lapjait hozzáadni
        final HandEvaluation evaluation = new HandEvaluation(hand.getCards(), new TreeSet<>());
        hand.setLatestHandEvaluation(evaluation);
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
                .map(Card::getRank)
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
                        .map(Card::getRank)
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
                    .getRank();
            final Rank currentHighest = highestHand
                    .getFirst()
                    .getRank();
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
                .filter(card -> card.getRank() == rank && card.getSymbol() == symbol)
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

        return getCardsForList(hand, map);
    }

    public @NonNull Set<TreeSet<Card>> makeDrill(@NonNull final Hand hand) {
        final Rank forDrill = getRankThatOccursAtLeast(hand, 3).getFirst();
        final Map<Rank, Integer> map = Map.of(forDrill, 3);

        return getCardsForList(hand, map);
    }

    private @NonNull TreeSet<Rank> getRankThatOccursAtLeast(@NonNull final Hand hand, final int occurrence) {
        return hand.getHandState().getRanks()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= occurrence)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private @NonNull Set<TreeSet<Card>> getCardsForList(
            @NonNull final Hand hand,
            @NonNull final Map<Rank, Integer> map) {

        int sizeOfFigure = 0;
        final Map<List<Card>, Integer> cardOccurrences = new HashMap<>();
        for (Map.Entry<Rank, Integer> entry : map.entrySet()) {
            List<Card> collections = hand.getCards().stream()
                    .filter(card -> card.getRank().equals(entry.getKey()))
                    .toList();
            cardOccurrences.put(collections, entry.getValue());
            sizeOfFigure += entry.getValue();
        }

        final TreeSet<Card> remainingCards = new TreeSet<>(hand.getCards());
        for (List<Card> cards : cardOccurrences.keySet()) {
            cards.forEach(remainingCards::remove);
        }
        final List<List<Card>> highCards = getNominatedHighCards(remainingCards);
        for (int i = 0; i < 5 - sizeOfFigure; i++) {
            cardOccurrences.put(highCards.get(i), 1);
        }

        Set<TreeSet<Card>> result = new HashSet<>();
        for (Map.Entry<List<Card>, Integer> entry : cardOccurrences.entrySet()) {
            result = collectVariationOfCards(result, entry);
        }

        return result;
    }

    private @NonNull Set<TreeSet<Card>> collectVariationOfCards(
            @NonNull final Set<TreeSet<Card>> previouses,
            @NonNull final Map.Entry<List<Card>, Integer> entry) {
        final Set<TreeSet<Card>> result = new HashSet<>();
        final Set<TreeSet<Card>> variations = getVariations(entry);
        if (previouses.isEmpty()) {
            return variations;
        }


        for (TreeSet<Card> previous : previouses) {
            for (TreeSet<Card> variation : variations) {
                final TreeSet<Card> next = new TreeSet<>();
                next.addAll(previous);
                next.addAll(variation);
                result.add(next);
            }
        }

        return result;
    }

    private @NonNull Set<TreeSet<Card>> getVariations(@NonNull final Map.Entry<List<Card>, Integer> entry) {
        final Set<TreeSet<Card>> variations = new HashSet<>();
        entry.getKey().forEach(card -> {
            if (entry.getValue() == 1) {
                final TreeSet<Card> variation = new TreeSet<>();
                variation.add(card);
                variations.add(variation);
            } else {
                final Set<Card> remaining = new HashSet<>(entry.getKey());
                remaining.remove(card);
                final Set<TreeSet<Card>> previousVariations = collectFurtherVariation(remaining, entry.getValue() - 1);
                for (TreeSet<Card> previousVariation : previousVariations) {
                    previousVariation.add(card);
                    variations.add(previousVariation);
                }
            }
        });
        return variations;
    }

    private @NonNull Set<TreeSet<Card>> collectFurtherVariation(@NonNull final Set<Card> remaining, final int size) {
        final Set<TreeSet<Card>> variations = new HashSet<>();
        for (Card card : remaining) {
            if (size == 1) {
                final TreeSet<Card> variation = new TreeSet<>();
                variation.add(card);
                variations.add(variation);
            } else {
                final Set<Card> nextRemaining = new TreeSet<>(remaining);
                nextRemaining.remove(card);
                final Set<TreeSet<Card>> previousVariations = collectFurtherVariation(nextRemaining, size - 1);
                for (TreeSet<Card> previousVariation : previousVariations) {
                    previousVariation.add(card);
                    variations.add(previousVariation);
                }
            }
        }

        return variations;
    }

    private static List<List<Card>> getNominatedHighCards(@NonNull final TreeSet<Card> remainingCards) {
        final Map<Rank, List<Card>> cardsByRank = new TreeMap<>();
        remainingCards.forEach(card -> {
            if (cardsByRank.containsKey(card.getRank())) {
                final List<Card> cards = cardsByRank.get(card.getRank());
                cards.add(card);
                cardsByRank.put(card.getRank(), cards);
            } else {
                final List<Card> cards = new ArrayList<>();
                cards.add(card);
                cardsByRank.put(card.getRank(), cards);
            }
        });

        return cardsByRank.values().stream().toList();
    }

    public @NonNull Set<TreeSet<Card>> makePoker(@NonNull final Hand hand) {
        final Rank poker = getRankThatOccursAtLeast(hand, 4).first();
        final Map<Rank, Integer> map = Map.of(poker, 4);

        return getCardsForList(hand, map);
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

        return getCardsForList(hand, map);
    }

    public Set<TreeSet<Card>> getHighestHand(@NonNull final Hand hand) {
        //final Set<TreeSet<Card>> highestHands = new HashSet<>();
        //highestHands.add(new TreeSet<>(new HashSet<>(hand.getCards()).stream().sorted().toList().subList(0, 5)));
        //return highestHands;
        return getCardsForList(hand, Collections.emptyMap());
    }

    public @NonNull Set<TreeSet<Card>> makeStraight(@NonNull final Hand hand) {
        final Set<TreeSet<Card>> highestHands = new HashSet<>();
        final List<Card> line = new HashSet<>(hand.getCards()).stream().sorted().toList();
        IntStream
                .range(0, line.size() - 4)
                .filter(i -> line.get(i).getRank().distance(line.get(i + 4).getRank()) == 4)
                .mapToObj(i -> new TreeSet<>(line.subList(i, i + 5)))
                .forEach(highestHand -> handleHighestHand(highestHand, highestHands, false));
        return highestHands;
    }

}
