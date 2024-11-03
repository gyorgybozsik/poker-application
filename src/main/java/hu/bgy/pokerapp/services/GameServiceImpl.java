package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.exceptions.ValidationException;
import hu.bgy.pokerapp.mappers.GameMapper;
import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.Table;
import hu.bgy.pokerapp.repositories.TableRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {
    private final TableServiceImpl tableService;
    private final GameMapper gameMapper;
    private final DeckServiceImpl deckService;
    private final TableRepo tableRepo;

    @Override
    public @NonNull TableDTO createGame(@NonNull final TableSetupDTO tableSetup) {
        final Table table = gameMapper.mapTableSetupToTable(tableSetup);
        return gameMapper.mapTableToTableDTO(tableRepo.save(table));
    }

    @Override
    public @NonNull TableDTO performTableSpeaker(@NonNull final SpeakerActionDTO speakerActionDTO, final UUID tableId) throws ValidationException {
        Table table = tableRepo.getReferenceById(tableId);
        table = tableService.performTableSpeaker(table, speakerActionDTO);
        tableRepo.save(table);
        return gameMapper.mapTableToTableDTO(table);
    }

    @Override
    public TableDTO deal(UUID tableId) {
        Table table = tableRepo.getReferenceById(tableId);
        //todo add validation hogy ez a 0. kör -e és még nincsenek kiosztott lapok
        for (Player player : table.getSeats()) {
            player.getHand().addCard(deckService.draw(table));
            player.getHand().addCard(deckService.draw(table));
        }
        table = tableRepo.save(table);
        return gameMapper.mapTableToTableDTO(table);
    }

    @Override
    public TableDTO loadGame(UUID tableId) {
        return gameMapper.mapTableToTableDTO(tableRepo.getReferenceById(tableId));
    }

    @Override
    public Set<UUID> getAllPokerTable() {
        final List<Table> tables = tableRepo.findAll();
        return gameMapper.mapTablesToIDs(tables);
    }
}
