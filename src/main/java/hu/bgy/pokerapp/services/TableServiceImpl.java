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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TableServiceImpl implements TableService {
    private final HandValueService handValueService;
    private final TableValidator tableValidator;
    private final DeckServiceImpl deckService;
    final Set<Poker> pokerKinds;

    private Poker getPokerKind(final @NonNull PokerType pokerType) {
        List<Poker> pokers = pokerKinds.stream().filter(poker -> poker.isPokerKind(pokerType)).toList();
        if (pokers.size() != 1) throw new IllegalStateException();
        return pokers.getFirst();
    }

    //todo kártya osztás asztalra és mentés

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
            if (isEndOfTheRound(table)) {
                return handleEndOfRound(table);
            }
            else {
                handleNextPartOfTheRound(table);
            }
        }
        return table;
    }
//todo a kódban lévő elnevezések rendszerezése és séma szerinti átnevezése
    private void handleNextPartOfTheRound(Table table) {
        table.setRound(table.getRound() + 1);
        RoundRole firstSpeaker = getFirstSpeaker(table);
        table.setSpeaker(firstSpeaker);
        table.setAfterLast(firstSpeaker);
        table = drawCardsForTable(table);
    }


    public Table handleEndOfRound(@NonNull Table table) {
        List<List<Player>> playersValues = seekingWinner(table);
        for (List<Player> entry : playersValues) {
            BigDecimal maximumBet = entry.stream()
                    .map(Player::getBalance)
                    .map(Balance::getBet)
                    .max(Comparator.naturalOrder())
                    .orElseThrow(IllegalStateException::new);
            while (BigDecimal.ZERO.compareTo(maximumBet) < 0) {
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
            }
        }
        return table;
    }

    public List<List<Player>> seekingWinner(@NonNull Table table) {
        Map<Value, List<Player>> results = new TreeMap<>();
        List<Player> ultimatePlayers = table.getActivePlayers();
        ultimatePlayers
                .forEach(player -> {
                    Value value = makeFinalHandAndValue(player, table);
                    if (results.containsKey(value)) {
                        results.get(value).add(player);
                    } else {
                        List<Player> next = new ArrayList<>();
                        next.add(player);
                        results.put(value, next);
                    }
                });
        List<List<Player>> order = results.entrySet()
                .stream()
                .flatMap(entry -> handValueService.orderWithHighestCard(entry.getKey(), entry.getValue()).stream())
                .collect(Collectors.toList());

        return order;//todo magaslap elkezelés
    }

    private Value makeFinalHandAndValue(@NonNull Player player,
                                        @NonNull Table table) {
        player.getHand().addCards(table.getCards());
        Value value = handValueService.evaluate(player.getHand());
        Set<Card> finalHand = handValueService.getValuesHand(player.getHand());
        player.getHand().throwCard(finalHand);
        return value;
    }

private static TreeSet<CardOwner> setMaker(final TreeSet<Card> cards) {
    TreeSet<CardOwner> cardO = new TreeSet<>(Comparator.comparing(CardOwner::getCard));
    cards.forEach(card -> {
        CardOwner e = new CardOwner();
        e.setCard(card);
        cardO.add(e);
    });
    return cardO;
}


    //todo majd imp
    private boolean isEndOfTheRound(Table table) {
        return table.getRound() == 3;
    }

    public Table drawCardsForTable(Table table) {
        int cardsSize = 0;
        if (table.getRound() == 1) cardsSize = 3;
        else if (table.getRound() > 1) cardsSize = 1;

        for (int i = 0; i < cardsSize; i++) {
            Card draw = deckService.draw(table);
            table.addCard(draw);
        }
        return table;
    }

    private RoundRole getFirstSpeaker(Table table) {
        RoundRole[] values = RoundRole.values();
        for (RoundRole speaker : values) {
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
