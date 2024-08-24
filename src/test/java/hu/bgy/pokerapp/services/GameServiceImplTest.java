package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.models.Table;
import hu.bgy.pokerapp.repositories.TableRepo;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static hu.bgy.pokerapp.enums.PokerType.TEXAS_HOLDEM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GameServiceImplTest {

    private final GameService gameService;

    @MockBean
    private TableRepo tableRepo;

    @Test
    void getAllPokerTableWithEmptyList() {
        //Given - Arrange (a teszt futásához szükséges paraméterek és feltétlek megteremtése)
        when(tableRepo.findAll()).thenReturn(List.of());

        //When - Act (ez a rész futtatja a tesztelendő metódust)
        Set<UUID> tables = gameService.getAllPokerTable();

        //Then - Assert (amikor meglett az eredmény te akkor ellenőrződ a helyességet)
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getAllPokerTableWithAValidTable() {
        //Given - Arrange
        final Table table = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table.setId(id);
        when(tableRepo.findAll()).thenReturn(List.of(table));

        //When - Act
        Set<UUID> tables = gameService.getAllPokerTable();

        //Then - Assert
        assertNotNull(tables);
        assertEquals(1, tables.size());
        assertTrue(tables.contains(id));
    }

    @Test
    void getAllPokerTableWithTwoValidTables() {
        //Given - Arrange
        final Table table1 = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id1 = UUID.fromString("4a98bab1-30a8-44d2-9273-e863e9d5e48b");
        table1.setId(id1);

        final Table table2 = new Table(TEXAS_HOLDEM, BigDecimal.TWO);
        final UUID id2 = UUID.fromString("9433b109-6e48-4d54-9880-594783332a99");
        table2.setId(id2);
        when(tableRepo.findAll()).thenReturn(List.of(table1, table2));

        //When - Act
        Set<UUID> tables = gameService.getAllPokerTable();

        //Then - Assert
        assertNotNull(tables);
        assertEquals(2, tables.size());
        assertTrue(tables.contains(id1));
        assertTrue(tables.contains(id2));
    }
}
