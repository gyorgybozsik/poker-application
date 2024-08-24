package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.enums.InGameState;
import hu.bgy.pokerapp.enums.RoundRole;
import hu.bgy.pokerapp.exceptions.ValidationException;
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

import static hu.bgy.pokerapp.enums.PokerType.TEXAS_HOLDEM;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class DeckServiceImplTest {
    private final TableService tableService;
    private final TableServiceImpl tableServiceImpl;
    private final DeckService deckService;
    private List<Player> fillPlayersList(int playersNumber, Table table) {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < playersNumber; i++) {
            String name = "Player" + " " + i;
            RoundRole[] roundRoles = RoundRole.values();
            final Player player = new Player(name, new BigDecimal(10), RoundRole.valueOf(String.valueOf(roundRoles[i])));
            player.setId(UUID.fromString(i + "a98bab1-30a8-44d2-9273-e863e9d5e48b"));
            player.getBalance().setBet(new BigDecimal(4));
            if (i % 2 == 0) player.getState().setInGameState(InGameState.SIT_OUT);
            playerList.add(player);
            player.setTable(table);
        }
        return playerList;
    }


    @Test
    void draw() throws ValidationException {
        //Given - Arrange (a teszt futásához szükséges paraméterek és feltétlek megteremtése)
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        table.setRound(0);

        final List<Player> seats = fillPlayersList(3, table);
        table.setSeats(seats);
        table.setSpeaker(RoundRole.SMALL_BLIND);
        table.setAfterLast(RoundRole.SMALL_BLIND);
        deckService.draw(table);
      // table.getSeats().getFirst().setHand();

        //When - Act (ez a rész futtatja a tesztelendő metódust)
        //  Table resultTable = tableService.handleEndOfRound(table, speakerActionDTO);
        //  assertLinesMatch(5, 5);
    }


}
