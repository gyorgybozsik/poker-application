package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import hu.bgy.pokerapp.exceptions.ValidationException;
import hu.bgy.pokerapp.models.Deck;
import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.Table;
import hu.bgy.pokerapp.services.poker.TableService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private List<Player> fillPlayersList(int playersNumber) {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < playersNumber; i++) {
            String name = "Player" + " " + i;
            RoundRole[] roundRoles = RoundRole.values();
            final Player player = new Player(name, new BigDecimal(10), RoundRole.valueOf(String.valueOf(roundRoles[i])));
            player.setId(UUID.fromString(i + "a98bab1-30a8-44d2-9273-e863e9d5e48b"));
            player.getBalance().setBet(new BigDecimal(4));
            if (i % 2 == 0) player.getState().setInGameState(InGameState.SIT_OUT);
            playerList.add(player);
        }
        return playerList;
    }


   private Table fillUpGameWithCards(Table table, int winners) {
       Deck deck = deckServiceImpl.remainingDeck(table);
       for (int i = 0; i< table.getSeats().size(); i++) {
           table.getSeats().get(i).setHand(deckServiceImpl.draw(table));
       }


       return table;
   }



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
        //Given - Arrange (a teszt futásához szükséges paraméterek és feltétlek megteremtése)
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(3);

        final List<Player> seats = fillPlayersList(9);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);
        tableServiceImpl.handleEndOfRound(table);

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

        final List<Player> seats = fillPlayersList(9);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);
      //  table = fillUpGameWithCards(table, 1);
        tableServiceImpl.handleEndOfRound(table);

        //When - Act (ez a rész futtatja a tesztelendő metódust)
      //  Table resultTable = tableService.handleEndOfRound(table, speakerActionDTO);
      //  assertLinesMatch(5, 5);
    }



}
