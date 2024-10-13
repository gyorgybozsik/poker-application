package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.components.TableValidator;
import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.enums.PokerType;
import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.RoundRole;
import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.exceptions.ValidationException;
import hu.bgy.pokerapp.models.*;
import hu.bgy.pokerapp.models.poker.Poker;
import hu.bgy.pokerapp.services.poker.TableService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RequiredArgsConstructor
@Service
public class TableServiceImpl implements TableService {
    private final HandValueService handValueService;
    private final TableValidator tableValidator;

    final Set<Poker> pokerKinds;

    private Poker getPokerKind(final @NonNull PokerType pokerType) {
        List<Poker> pokers = pokerKinds.stream().filter(poker -> poker.isPokerKind(pokerType)).toList();
        if (pokers.size() != 1) throw new IllegalStateException();
        return pokers.getFirst();
    }

    @Override
    public @NonNull Table performTableSpeaker(@NonNull Table table, @NonNull SpeakerActionDTO speakerActionDTO) throws ValidationException {
        List<String> massages = tableValidator.validateSpeakerAction(table, speakerActionDTO);
        if (!massages.isEmpty()) {
            throw new ValidationException(massages);
        }
        Player player = table.getPlayer(speakerActionDTO.playerId());
        Player speaker = table.getSpeakerPlayer();
        switch (speakerActionDTO.playerAction()) {
            case CALL -> speaker.bet(speakerActionDTO.changeAmount());
            case RAISE -> {
                speaker.bet(speakerActionDTO.changeAmount());
                table.setAfterLast(speaker.getState().getRoundRole());
            }
            case FOLD -> player.fold();
        }
        return handleNextSpeaker(table);
    }

    private Table handleNextSpeaker(Table table) {
        Optional<RoundRole> roundRole = nextSpeaker(table);
        if (roundRole.isPresent()) {
            table.setSpeaker(roundRole.get());
        } else {
            if (isEndOfTheGame(table)) {
                return handleEndOfRound(table);
            }
        }return table;
    }


    public Table handleEndOfRound(Table table) {
        List<List<Player>>  playersValues = seekingWinner(table);
        for (List<Player>  entry : playersValues) {
            BigDecimal maximumBet = entry.stream()
                    .map(Player::getBalance)
                    .map(Balance::getBet)
                    .max(Comparator.naturalOrder())
                    .orElseThrow(IllegalStateException::new);
            while (BigDecimal.ZERO.compareTo(maximumBet) == 0) {
                BigDecimal minimumBet = entry.stream()
                        .map(Player::getBalance)
                        .map(Balance::getBet)
                        .filter(bigDecimal -> BigDecimal.ZERO.compareTo(bigDecimal) != 0)
                        .min(Comparator.naturalOrder())
                        .orElseThrow(IllegalStateException::new);
                BigDecimal distribute = BigDecimal.ZERO;
                List<Player> seats = table.getSeats();
                for (Player player : seats) {
                    distribute = distribute.add(player.getBalance().deductBet(minimumBet));
                }
                final BigDecimal distributeCash = distribute.divide(BigDecimal.valueOf(entry.size()), 2, RoundingMode.HALF_DOWN);
                entry.forEach(player -> player.getBalance().addCash(distributeCash));
                entry.removeIf(Player::hasNoBet);
                maximumBet = maximumBet.subtract(minimumBet);

                //todo teszteket készíteni
            }
        }return table;
    }

    //todo azonos kezeknél
    private void splittingThePot(Table table, List<Player> winners) {
        Set<Integer> cd = new HashSet<>();
        cd.contains(1);
    }


    public List<List<Player>>  seekingWinner(Table table) {
        Map<Value, List<Player>> results = new TreeMap<>();
        List<Player> ultimatePlayers = table.getActivePlayers();
        for (Player player : ultimatePlayers) {
            Value value = makeFinalHandAndValue(player, table);
            if (results.containsKey(value)) {
                results.get(value).add(player);
            } else {
                List<Player> next = new ArrayList<>();
                next.add(player);
                results.put(value, next);
            }
        }
        List<List<Player>> order = new ArrayList<>();
        for (Map.Entry<Value, List<Player>> entry : results.entrySet()) {
            order.addAll(handValueService.orderWithHighestCard(entry.getKey(), entry.getValue()));
        }
        return order;//todo magaslap elkezelés
    }

    private Value makeFinalHandAndValue(Player player, Table table) {
        player.getHand().addCard(table.getCards());
        Value value = handValueService.evaluate(player.getHand());
        Set<Card> finalHand = handValueService.getValuesHand(player.getHand());
        player.getHand().throwCard(finalHand);
        return value;
    }

    public List<Player> theBestValue(Table table, List<Player> players, Value value) {
        List<Player> winner = new ArrayList<>();
        switch (value) {
            case ROYAL_FLUSH -> {
                return players;
            }
            case POKER, FULL_HOUSE, DRILL, TWO_PAIRS, PAIR -> winner = highCombination(players, table);
        }
        return winner;
    }

    private List<Player> highCombination(List<Player> players, Table table) {
        List<Player> winners = new ArrayList<>();
        Map<Player, Map<Rank, Integer>> details = new HashMap<>();
        for (Player player : players) {
            Map<Rank, Integer> cards = fillRanks(player.getHand().getCards());
            details.put(player, cards);
        }
        return winners;
    }

    public @NonNull Map<Rank, Integer> fillRanks(@NonNull final TreeSet<Card> cards) {
        final Map<Rank, Integer> ranks = new HashMap<>();
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

    private List<Player> highCard(List<Player> players, Table table) {
        List<Player> winner = new ArrayList<>();
        for (Player player : players) {
            if (winner.isEmpty()) {
                winner.add(player);
                continue;
            }
            if (player.getHand().getCards().getFirst().getRank().isHigher(winner.getFirst().getHand().getCards().getFirst().getRank())) {
                winner.clear();
                winner.add(player);
            }
        }
        return winner;
    }


    private Hand setMaker(Player player, final TreeSet<Card> cards) {
        final TreeSet<CardOwner> cardO = new TreeSet<>(Comparator.comparing(CardOwner::getCard));
        cards.forEach(card -> {
            CardOwner e = new CardOwner();
            e.setCard(card);
            cardO.add(e);
        });
        return new Hand(cardO);
    }

    //todo majd imp
    private boolean isEndOfTheGame(Table table) {
        if (table.getRound() == 3) {
            return true;
        }
        table.setRound(table.getRound() + 1);
        RoundRole firstSpeaker = getFirstSpeaker(table);
        table.setSpeaker(firstSpeaker);
        table.setAfterLast(firstSpeaker);
        return false;
    }

    private RoundRole getFirstSpeaker(Table table) {
        for (RoundRole speaker : RoundRole.values()) {
            final RoundRole role = speaker;
            if (table.getSeats().stream().anyMatch(player -> player.isSpeakable(role))) {
                return speaker;
            }
        }
        throw new IllegalStateException("Couldn't find first speaker");
    }

    private Optional<Integer> getNextRound(Table table) {
        return table.getRound() == 3 ? Optional.empty() : Optional.of(table.getRound() + 1);
    }


    public Optional<RoundRole> nextSpeaker(final @NonNull Table table) {
        for (RoundRole roundRole = table
                .getSpeaker()
                .nextRole(); roundRole != table
                .getAfterLast(); roundRole = roundRole
                .nextRole()) {
            final RoundRole role = roundRole;
            if (table.getSeats()
                    .stream()
                    .anyMatch(player -> player.isSpeakable(role))) {
                return Optional.of(roundRole);
            }
        }
        return Optional.empty();
    }
}
// írd át ezt az egy metódust
///todo TESZTET a round role nextRole() metódusra SPRING
//todo validáció kiegészítés (csak akkor lehessen az opciót alkalmazni ha tényleg jogos)


//todo HandValueServicbe átservezni a magyaslap elkezelést
//todo split pot ide tableservic-be
