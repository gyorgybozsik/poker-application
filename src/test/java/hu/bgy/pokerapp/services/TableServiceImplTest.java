package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.enums.*;
import hu.bgy.pokerapp.exceptions.ValidationException;
import hu.bgy.pokerapp.models.*;
import hu.bgy.pokerapp.services.poker.TableService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static hu.bgy.pokerapp.enums.PlayerAction.FOLD;
import static hu.bgy.pokerapp.enums.PokerType.TEXAS_HOLDEM;
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
    }   private List<Player> fillPlayersList2(Table table, int playersNumber) {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            String name = "Player" + " " + i;
            RoundRole[] roundRoles = RoundRole.values();
            final Player player = new Player(name, new BigDecimal(100), RoundRole.valueOf(String.valueOf(roundRoles[i])));
            player.setId(UUID.fromString(i + "a98bab1-30a8-44d2-9273-e863e9d5e48b"));
            player.getBalance().setBet(new BigDecimal(10));
            playerList.add(player);
            player.setTable(table);
        }
        return playerList;
    }

    //  private void getPair(Table table, int number){
    //      Map<org.springframework.beans.factory.annotation.Value, List<Card>> series = new HashMap<>();
    //      for (int i = 0; ) {
    //          Rank[] ranks = Rank.values();
    //      }
    //      List<Card> pair = new ArrayList<>();
    //      pair.add(card(Symbol.HEARTH, Rank.JACK));
    //      pair.add(card(Symbol.SPADE, Rank.JACK));
    //      cards.add(pair); pair.clear();
    //      pair.add(card(Symbol.DIAMOND, Rank.JACK));
    //      pair.add(card(Symbol.CLUB, Rank.JACK));
    //      cards.add(pair); pair.clear();
    //      pair.add(card(Symbol.DIAMOND, Rank.KING));
    //      pair.add(card(Symbol.CLUB, Rank.KING));
    //      cards.add(pair); pair.clear();
    //      pair.add(card(Symbol.HEARTH, Rank.KING));
    //      pair.add(card(Symbol.SPADE, Rank.KING));
    //  }

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
        cards.add(card(Symbol.CLUB, Rank.TEN));
        cards.add(card(Symbol.SPADE, Rank.JACK));
        table.getSeats().get(0).setHand(new Hand(setMaker(table.getSeats().get(0), table, cards)));
        cards.clear();
        cards.add(card(Symbol.DIAMOND, Rank.TEN));
        cards.add(card(Symbol.CLUB, Rank.JACK));
        table.getSeats().get(1).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));
        cards.clear();
        cards.add(card(Symbol.DIAMOND, Rank.SIX));
        cards.add(card(Symbol.CLUB, Rank.TEN));
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
        cards.add(card(Symbol.HEARTH, Rank.SEVEN));
        cards.add(card(Symbol.CLUB, Rank.EIGHT));
        cards.add(card(Symbol.SPADE, Rank.NINE));
        cards.add(card(Symbol.DIAMOND, Rank.TWO));
        cards.add(card(Symbol.HEARTH, Rank.ACE));
        return cards;
    }

    private TreeSet<CardOwner> setMaker(final Player player, Table table, final TreeSet<Card> cards) {
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

    //--------------------------------------------------------------------------------------------------------------
    @Test
    void performTableSpeakerFold() throws ValidationException {
        //Given - Arrange (a teszt futásához szükséges paraméterek és feltétlek megteremtése)
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(0);
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
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(0);
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

    @Test
    void handleEndOfRound() throws ValidationException {
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(3);
        final List<Player> seats = fillPlayersList2(table, 2);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);

        TreeSet<Card> cards;
        cards = getStraight();
        table.setCards(setMaker(null, table, cards));
        cards.clear();
        cards.add(card(Symbol.CLUB, Rank.TEN));
        cards.add(card(Symbol.SPADE, Rank.JACK));
        table.getSeats().get(0).setHand(new Hand(setMaker(table.getSeats().get(0), table, cards)));
        cards.clear();
        cards.add(card(Symbol.DIAMOND, Rank.ACE));
        cards.add(card(Symbol.CLUB, Rank.TWO));
        table.getSeats().get(1).setHand(new Hand(setMaker(table.getSeats().get(1), table, cards)));

        Table resultTable = tableServiceImpl.handleEndOfRound(table);
        assertEquals(new BigDecimal(120), resultTable.getSeats().get(0).getBalance().getCash());
        assertEquals(new BigDecimal(100), resultTable.getSeats().get(1).getBalance().getCash());

        //When - Act (ez a rész futtatja a tesztelendő metódust)
        //  Table resultTable = tableService.handleEndOfRound(table, speakerActionDTO);
        //  assertLinesMatch(5, 5);
    }

    @Test
    void seekingWinner() throws ValidationException {
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(3);

        final List<Player> seats = fillPlayersList(table, 5, true);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);
        fillUpGameWithShareHands(table);
        tableServiceImpl.seekingWinner(table);

        //When - Act (ez a rész futtatja a tesztelendő metódust)

        //  Table resultTable = tableService.handleEndOfRound(table, speakerActionDTO);
        //  assertLinesMatch(5, 5);
    }

    @Test
    void theBestValue() {
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(3);
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

    //  public static Stream<Arguments> getTest() {
    //      return Stream.of(
    //              Arguments.of(
    //              Set.of(
    //                      of(card(CLUB, ACE), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(SPADE, JACK), card(SPADE, NINE))),
    //              NOTHING,
    //              of(card(SPADE, NINE), card(SPADE, JACK), card(DIAMOND, KING), card(DIAMOND, QUEEN), card(CLUB, TWO),
    //                      card(SPADE, THREE), card(DIAMOND, FIVE), card(DIAMOND, EIGHT),
    //                      card(CLUB, ACE), card(HEARTH, SEVEN)))
//
    //      );
    //  }
    //  @ParameterizedTest
    //  @MethodSource(value = "getTest")
    //  void testGetValue(final Map<Player, Value> expected, final Value value, final TreeSet<Card> cards) {
    //      Set<TreeSet<Card>> handBasedOnValue = handValueService.getHandBasedOnValue(value, hand(cards));
    //      assertTrue(handBasedOnValue.containsAll(expected));
    //      assertEquals(expected, handBasedOnValue);
    //  }
    public static Card card(final Symbol symbol, final Rank rank) {
        return new Card(UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b"), symbol, rank);
    }
}
