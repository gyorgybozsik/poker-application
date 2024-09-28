package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.components.TableValidator;
import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.enums.PokerType;
import hu.bgy.pokerapp.enums.RoundRole;
import hu.bgy.pokerapp.enums.Value;
import hu.bgy.pokerapp.exceptions.ValidationException;
import hu.bgy.pokerapp.models.*;
import hu.bgy.pokerapp.models.poker.Poker;
import hu.bgy.pokerapp.services.poker.TableService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
        handleNextSpeaker(table);

        return table;
    }

    private void handleNextSpeaker(Table table) {
        Optional<RoundRole> roundRole = nextSpeaker(table);
        roundRole.ifPresentOrElse(
                roundRole1 -> table.setSpeaker(roundRole1),
                () -> {
                    Optional<Integer> round = getNextRound(table);
                    round.ifPresentOrElse(
                            nextRound -> handleNextRound(table, nextRound),
                            () -> {
                                handleEndOfRound(table);
                            }
                    );
                }
        );
    }


    public void handleEndOfRound(Table table) {
        List<Player> ultimatePlayers = table
                .getSeats()
                .stream()
                .filter(Player::isActive)
                .toList();
        List<Player> winners = seekingWinner(table, ultimatePlayers);
        splittingThePot(table, winners);


    }

    private void splittingThePot(Table table, List<Player> winners) {

    }

    public List<Player> seekingWinner(Table table, List<Player> ultimatePlayers) {
        Map<Player, Value> results = new HashMap<>();
        for (Player player : ultimatePlayers) {
            Set<Card> finalFiveCard = handValueService.getValuesHand(setMaker(player.getCompleteCards(player, table)));
            Value value = handValueService.evaluate(setMaker(new TreeSet<>(finalFiveCard)));
            results.put(player, value);
            player.setHand(setMaker(new TreeSet<>(finalFiveCard)));
        }
        return theBestValue(results);
    }

    public List<Player> theBestValue(Map<Player, Value> results) {
        Value highestValue = results.values().stream().max(Comparator.naturalOrder()).orElse(Value.NOTHING);
        return results.entrySet().stream()
                .filter(entry -> entry.getValue() == highestValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    private Hand setMaker(final TreeSet<Card> cards) {
        final TreeSet<CardOwner> cardO = new TreeSet<>(Comparator.comparing(CardOwner::getCard));
        cards.forEach(card -> {
            CardOwner e = new CardOwner();
            e.setCard(card);
            cardO.add(e);
        });
        return new Hand(cardO);
    }

    //todo majd imp
    private void handleNextRound(Table table, Integer nextRound) {
        table.setRound(nextRound);
        RoundRole firstSpeaker = getFirstSpeaker(table);
        table.setSpeaker(firstSpeaker);
        table.setAfterLast(firstSpeaker);
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
            if (table.getSeats().stream().anyMatch(player -> player.isSpeakable(role))) {
                return Optional.of(roundRole);
            }
        }
        return Optional.empty();
    }
}
// írd át ezt az egy metódust
///todo TESZTET a round role nextRole() metódusra SPRING
//todo validáció kiegészítés (csak akkor lehessen az opciót alkalmazni ha tényleg jogos)