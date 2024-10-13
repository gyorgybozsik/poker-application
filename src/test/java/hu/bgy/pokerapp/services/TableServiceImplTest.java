package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.enums.*;
import hu.bgy.pokerapp.exceptions.ValidationException;
import hu.bgy.pokerapp.models.*;
import hu.bgy.pokerapp.services.poker.TableService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hu.bgy.pokerapp.enums.PlayerAction.FOLD;
import static hu.bgy.pokerapp.enums.PokerType.TEXAS_HOLDEM;
import static hu.bgy.pokerapp.enums.Rank.*;
import static hu.bgy.pokerapp.enums.Symbol.*;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class TableServiceImplTest {
    private final TableService tableService;
    private final TableServiceImpl tableServiceImpl;
    private final DeckServiceImpl deckServiceImpl;

    private List<Player> fillPlayersList(Table table, int playersNumber, boolean onlyActive) {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < playersNumber; i++) {
            String name = "Player" + " " + i;
            RoundRole[] roundRoles = RoundRole.values();
            final Player player = new Player(name, new BigDecimal(10), RoundRole.valueOf(String.valueOf(roundRoles[i])));
            player.setId(UUID.fromString(i + "a98bab1-30a8-44d2-9273-e863e9d5e48b"));
            player.getBalance().setBet(new BigDecimal(4));
            if (!onlyActive) {
                if (i % 2 == 0) player.getState().setInGameState(InGameState.SIT_OUT);
                playerList.add(player);
            } else playerList.add(player);
            player.setTable(table);
        }
        return playerList;
    }

    private List<Player> fillPlayersList2(Table table, int playersNumber) {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < playersNumber; i++) {
            String name = "Player" + " " + i;
            RoundRole[] roundRoles = RoundRole.values();
            final Player player = new Player(name, new BigDecimal(100), RoundRole.valueOf(String.valueOf(roundRoles[i])));
            player.setId(UUID.fromString(i + "a98bab1-30a8-44d2-9273-e863e9d5e48b"));
            player.getBalance().setBet(new BigDecimal(20));
            playerList.add(player);
            player.setTable(table);
        }
        return playerList;
    }

    private List<TreeSet<Card>> fillCombinations() {
        List<TreeSet<Card>> combinations = new ArrayList<>();
        TreeSet<Card> cards = new TreeSet<>();
        cards.add(card(HEARTH, ACE));
        cards.add(card(CLUB, SIX));
        cards.add(card(CLUB, EIGHT));
        cards.add(card(SPADE, TEN));
        cards.add(card(CLUB, TEN));
        combinations.add(new TreeSet<>(cards));
        cards.clear();

        //todo 2pár ász-10
        cards.add(card(DIAMOND, ACE));
        cards.add(card(DIAMOND, SIX));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo 2pár ász-10       split
        cards.add(card(SPADE, ACE));
        cards.add(card(DIAMOND, EIGHT));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo drill Ász-8 magaslappal
        cards.add(card(HEARTH, TEN));
        cards.add(card(DIAMOND, TWO));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo drill Ász-Király magaslappal
        cards.add(card(DIAMOND, TEN));
        cards.add(card(CLUB, KING));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo sor
        cards.add(card(DIAMOND, SEVEN));
        cards.add(card(HEARTH, NINE));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo szín
        cards.add(card(CLUB, FIVE));
        cards.add(card(CLUB, FOUR));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo szín ász magaslappal
        cards.add(card(CLUB, TWO));
        cards.add(card(CLUB, ACE));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo 6-os full
        cards.add(card(SPADE, SIX));
        cards.add(card(HEARTH, SIX));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo 8-os full
        cards.add(card(SPADE, EIGHT));
        cards.add(card(HEARTH, EIGHT));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        //todo színsor
        cards.add(card(CLUB, SEVEN));
        cards.add(card(CLUB, NINE));
        combinations.add(new TreeSet<>(cards));
        cards.clear();
        return combinations;
    }


    private void handCombinationsForPlayers(Table table, int memberNeeded) {
        List<TreeSet<Card>> combinations = fillCombinations();
        table.setCards(setMaker(null, table, combinations.getFirst()));
        combinations.removeFirst();
        for (int i = 0; i < memberNeeded; i++) {
            table.getSeats().get(i).setHand(new Hand(setMaker(table.getSeats().get(i), table, combinations.get(i))));
        }


    }


    private void fillUpGameWithCards(Table table) {
        TreeSet<Card> cards = new TreeSet<>();
        //   int x = 0;
        //   if (winner > 0) {
        //       for (int y = 0; y < winner; y++) {
        //       }
        //   }
        for (int i = 0; i < table.getSeats().size(); i++) {
            Player player = table.getSeats().get(i);
            if (player.getHand() != null) continue;
            cards.addAll(drawingCards(table, 2));
            table.getSeats().get(i).setHand(new Hand(setMaker(player, table, cards)));
            cards.clear();
        }
        cards.addAll(drawingCards(table, 5));
        table.setCards(setMaker(null, table, cards));
    }

    private void changeBalanceVolume(Table table) {
        Random random = new Random();
        BigDecimal amountBalance = BigDecimal.valueOf(100);
        BigDecimal amountBet = BigDecimal.valueOf(20);
        for (Player player : table.getSeats()) {
            player.getBalance().addCash(amountBalance);
            amountBalance = amountBalance.subtract(BigDecimal.valueOf(20));
            player.getBalance().addCash(amountBet);
        }
    }

    private void fillUpGameWithShareHands(Table table) {
        TreeSet<Card> cards;
        cards = getStraight();
        table.setCards(setMaker(null, table, cards));
        cards.clear();
        cards.add(card(CLUB, TEN));
        cards.add(card(SPADE, JACK));
        table.getSeats().get(0).setHand(new Hand(setMaker(table.getSeats().get(0), table, cards)));
        cards.clear();
        cards.add(card(DIAMOND, TEN));
        cards.add(card(CLUB, JACK));
        table.getSeats().get(1).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
        cards.clear();
        cards.add(card(DIAMOND, SIX));
        cards.add(card(CLUB, TEN));
        table.getSeats().get(2).setHand(new Hand(setMaker(table.getSeats().get(2), table, cards)));
        cards.clear();
        for (int i = 3; i < table.getSeats().size(); i++) {
            Player player = table.getSeats().get(i);
            cards.addAll(drawingCards(table, 2));
            table.getSeats().get(i).setHand(new Hand(setMaker(player, table, cards)));
            cards.clear();
        }
    }

    private TreeSet<Card> getStraight() {
        TreeSet<Card> cards = new TreeSet<>();
        cards.add(card(HEARTH, SEVEN));
        cards.add(card(CLUB, EIGHT));
        cards.add(card(SPADE, NINE));
        cards.add(card(DIAMOND, TWO));
        cards.add(card(HEARTH, ACE));
        return cards;
    }

    private static TreeSet<CardOwner> setMaker(final Player player, Table table, final TreeSet<Card> cards) {
        TreeSet<CardOwner> cardO = new TreeSet<>(Comparator.comparing(CardOwner::getCard));
        cards.forEach(card -> {
            CardOwner e = new CardOwner();
            e.setPlayer(player);
            e.setTable(table);
            e.setCard(card);
            cardO.add(e);
        });
        return cardO;
    }

    private TreeSet<Card> drawingCards(Table table, int neededNumber) {
        Random random = new Random();
        TreeSet<Card> cards = new TreeSet<>();
        IntStream.range(0, neededNumber)
                .mapToObj(i -> deckServiceImpl.remainingDeck(table))
                .forEach(deck -> {
                    Card card = deck.getDeck().get(random.nextInt(deck.size()));
                    cards.add(card);
                    deck.remove(card);
                });
        return cards;
    }

    @NotNull
    private static Table getTable(int round) {
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(round);
        return table;
    }

    //--------------------------------------------------------------------------------------------------------------
    @Test
    void performTableSpeakerFold() throws ValidationException {
        //Given - Arrange (a teszt futásához szükséges paraméterek és feltétlek megteremtése)
        final Table table = getTable(0);

        //10 egységgel kezdtek a játékosok
        final Player gyuri = new Player("Gyuri", new BigDecimal(6), RoundRole.BIG_BLIND);
        gyuri.setId(UUID.fromString("8a98bab1-30a8-44d2-9273-e863e9d5e48b"));
        gyuri.getBalance().setBet(new BigDecimal(4));
        final Player zoli = new Player("Zoli", new BigDecimal(8), RoundRole.SMALL_BLIND);
        UUID zoliId = UUID.fromString("2a98bab1-30a8-44d2-9273-e863e9d5e48b");
        zoli.getBalance().setBet(new BigDecimal(2));
        zoli.setId(zoliId);

        final List<Player> seats = List.of(gyuri, zoli);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);

        final SpeakerActionDTO speakerActionDTO = new SpeakerActionDTO(zoliId, FOLD, ZERO);

        //When - Act (ez a rész futtatja a tesztelendő metódust)
        Table resultTable = tableService.performTableSpeaker(table, speakerActionDTO);

        //Then - Assert (amikor meglett az eredmény te akkor ellenőrződ a helyességet)
        assertEquals(new BigDecimal(8), zoli.getBalance().getCash());
        assertEquals(ZERO, zoli.getBalance().getBet());
        assertEquals(new BigDecimal(12), gyuri.getBalance().getCash());
        assertEquals(ZERO, gyuri.getBalance().getBet());
    }

    @Test
    void getAllPokerTableValidationError() throws ValidationException {
        //Given - Arrange (a teszt futásához szükséges paraméterek és feltétlek megteremtése)
        final Table table = getTable(0);
        //10 egységgel kezdtek a játékosok
        final Player gyuri = new Player("Gyuri", new BigDecimal(6), RoundRole.BIG_BLIND);
        UUID gyuriId = UUID.fromString("8a98bab1-30a8-44d2-9273-e863e9d5e48b");
        gyuri.setId(gyuriId);
        final Player zoli = new Player("Zoli", new BigDecimal(8), RoundRole.SMALL_BLIND);
        UUID zoliId = UUID.fromString("2a98bab1-30a8-44d2-9273-e863e9d5e48b");
        zoli.setId(zoliId);

        final List<Player> seats = List.of(gyuri, zoli);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);

        final SpeakerActionDTO speakerActionDTO = new SpeakerActionDTO(gyuriId, FOLD, ZERO);

        //When - Act & Then - Assert
        assertThrows(ValidationException.class, () -> tableService.performTableSpeaker(table, speakerActionDTO));
    }

    //@Test
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10})
    void handleEndOfRound(int playersNumber) throws ValidationException {
        List<BigDecimal> testVariables = loadVariables(playersNumber);
        final Table table = getTable(3);
        final List<Player> seats = fillPlayersList2(table, playersNumber);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);

        handCombinationsForPlayers(table, playersNumber);

        Table resultTable = tableServiceImpl.handleEndOfRound(table);
        IntStream.range(0, playersNumber).forEach(x -> {
            BigDecimal expectedBalance = new BigDecimal(String.valueOf(testVariables.get(x))).setScale(2);
            BigDecimal actualBalance = resultTable.getSeats().get(x).getBalance().getCash().setScale(2);
            assertEquals(expectedBalance, actualBalance);
        });
    }

    private static Player creatPlayer(Set<Card> cards, Integer cash, Integer bet, RoundRole roundRole) {
        Player player = new Player();
        player.setBalance(Balance.builder().player(player).cash(BigDecimal.valueOf(cash)).bet(BigDecimal.valueOf(bet)).build());
        Hand hand = new Hand();
        player.setState(new PlayerState(player, roundRole));
        player.setHand(hand);
        hand.setCardOwners(setMaker(player, null, new TreeSet<>(cards)));
        return player;
    }

    private static Stream<Arguments> handleEndOfRoundCases() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                creatPlayer(Set.of(card(DIAMOND, ACE), card(DIAMOND, SIX)), 100, 20, RoundRole.BIG_BLIND),
                                creatPlayer(Set.of(card(SPADE, ACE), card(DIAMOND, EIGHT)), 100, 20, RoundRole.SMALL_BLIND),
                                creatPlayer(Set.of(card(HEARTH, TEN), card(DIAMOND, TWO)), 100, 20, RoundRole.SPEAKER_1)),

                        Set.of(card(HEARTH, ACE), card(CLUB, SIX), card(CLUB, EIGHT), card(SPADE, TEN), card(CLUB, TEN)),
                        List.of(
                                Balance.builder().cash(new BigDecimal("100.00")).bet(new BigDecimal("0")).build(),
                                Balance.builder().cash(new BigDecimal("100.00")).bet(new BigDecimal("0")).build(),
                                Balance.builder().cash(new BigDecimal("160.00")).bet(new BigDecimal("0")).build())
                ),
                Arguments.of(
                        List.of(
                                creatPlayer(Set.of(card(DIAMOND, ACE), card(DIAMOND, SIX)), 100, 20, RoundRole.BIG_BLIND),
                                creatPlayer(Set.of(card(SPADE, ACE), card(DIAMOND, EIGHT)), 100, 20, RoundRole.SMALL_BLIND)),

                        Set.of(card(HEARTH, ACE), card(CLUB, SIX), card(CLUB, EIGHT), card(SPADE, TEN), card(CLUB, TEN)),
                        List.of(
                                Balance.builder().cash(new BigDecimal("120.00")).bet(new BigDecimal("0")).build(),
                                Balance.builder().cash(new BigDecimal("120.00")).bet(new BigDecimal("0")).build())
                )
        );
    }
    //todo egyik játékosnak nyernie kellene, de mégis osztozik

    @ParameterizedTest
    @MethodSource(value = "handleEndOfRoundCases")
    void handleEndOfRound2(List<Player> seats, Set<Card> tableCards, List<Balance> expectedBalance) {
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(3);
        table.setCards(setMaker(null, table, new TreeSet<>(tableCards)));
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);


        Table resultTable = tableServiceImpl.handleEndOfRound(table);
        assertEquals(expectedBalance.size(), seats.size());
        for (int i = 0; i != expectedBalance.size(); i++) {
            assertEquals(expectedBalance.get(i).getCash(), resultTable.getSeats().get(i).getBalance().getCash());
            assertEquals(expectedBalance.get(i).getBet(), resultTable.getSeats().get(i).getBalance().getBet());
        }
    }

    private List<BigDecimal> loadVariables(int serialNumber) {
        List<BigDecimal> testVariables = new ArrayList<>();
        for (int i = 0; i < serialNumber; i++) {
            int result = 100;
            if (serialNumber == 2) result += 20;
            if (i == serialNumber - 1 && serialNumber != 2) result = result + (20 * serialNumber);
            BigDecimal bigDecimalValue = new BigDecimal(result).setScale(2);
            testVariables.add(bigDecimalValue);
        }
        return testVariables;
    }

    private void clearForNext(Table table) {
        table.getSeats().clear();
        table.getCards().clear();
    }


    @Test
    void seekingWinner() throws ValidationException {
        final Table table = getTable(3);
        final List<Player> seats = fillPlayersList(table, 10, true);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);
        handCombinationsForPlayers(table, 10);
        tableServiceImpl.seekingWinner(table);

        //When - Act (ez a rész futtatja a tesztelendő metódust)

        //  Table resultTable = tableService.handleEndOfRound(table, speakerActionDTO);
        //  assertLinesMatch(5, 5);
    }

    @Test
    void theBestValue() {
        final Table table = getTable(3);
        List<Player> seats = fillPlayersList(table, 5, true);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);
        //todo egy nyertes
        Map<Player, Value> results = new HashMap<>();
        results.put(seats.getFirst(), Value.STRAIGHT);
        Player playerX = seats.getFirst();
        seats.removeFirst();
        seats.forEach(player -> results
                .put(player, Value.NOTHING));
        //List<Player> winners = tableServiceImpl.theBestValue(results);
        //assertEquals(1, winners.size());
        //assertEquals(playerX, winners.get(0));
        //todo két nyertes / osztozás
        seats.clear();
        seats = fillPlayersList(table, 6, true);
        results.put(seats.getLast(), Value.STRAIGHT);
        Player playerY = seats.getLast();
        //assertEquals(2, winners.size());
        //assertEquals(playerX, winners.get(0));
        //assertEquals(playerY, winners.get(1));
    }

    //private void kkk() {
    //    cards.add(card(Symbol.HEARTH, Rank.ACE));
    //    cards.add(card(Symbol.CLUB, Rank.SIX));
    //    cards.add(card(Symbol.CLUB, Rank.EIGHT));
    //    cards.add(card(Symbol.SPADE, Rank.TEN));
    //    cards.add(card(Symbol.CLUB, Rank.TEN));
    //    table.setCards(setMaker(null, table, cards));
    //    cards.clear(); //todo 2pár ász-10
    //    cards.add(card(Symbol.DIAMOND, Rank.ACE));
    //    cards.add(card(Symbol.DIAMOND, Rank.SIX));
    //    table.getSeats().get(0).setHand(new Hand(setMaker(table.getSeats().get(0), table, cards)));
    //    cards.clear(); //todo 2pár ász-10
    //    cards.add(card(Symbol.SPADE, Rank.ACE));
    //    cards.add(card(Symbol.DIAMOND, Rank.EIGHT));
    //    table.getSeats().get(1).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo drill Ász-8 magaslappal
    //    cards.add(card(Symbol.HEARTH, Rank.TEN));
    //    cards.add(card(Symbol.DIAMOND, Rank.TWO));
    //    table.getSeats().get(2).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo drill Ász-Király magaslappal
    //    cards.add(card(Symbol.DIAMOND, Rank.TEN));
    //    cards.add(card(Symbol.CLUB, Rank.KING));
    //    table.getSeats().get(3).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo sor
    //    cards.add(card(Symbol.DIAMOND, Rank.SEVEN));
    //    cards.add(card(Symbol.HEARTH, Rank.NINE));
    //    table.getSeats().get(4).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo szín
    //    cards.add(card(Symbol.CLUB, Rank.FIVE));
    //    cards.add(card(Symbol.CLUB, Rank.FOUR));
    //    table.getSeats().get(5).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo szín ász magaslappal
    //    cards.add(card(Symbol.CLUB, Rank.TWO));
    //    cards.add(card(Symbol.CLUB, Rank.ACE));
    //    table.getSeats().get(6).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo 6-os full
    //    cards.add(card(Symbol.SPADE, Rank.SIX));
    //    cards.add(card(Symbol.HEARTH, Rank.SIX));
    //    table.getSeats().get(7).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo 8-os full
    //    cards.add(card(Symbol.SPADE, Rank.EIGHT));
    //    cards.add(card(Symbol.HEARTH, Rank.EIGHT));
    //    table.getSeats().get(8).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear(); //todo színsor
    //    cards.add(card(Symbol.CLUB, Rank.SEVEN));
    //    cards.add(card(Symbol.CLUB, Rank.NINE));
    //    table.getSeats().get(9).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
    //    cards.clear();
    //}
    public static Card card(final Symbol symbol, final Rank rank) {
        return new Card(UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b"), symbol, rank);
    }

}
