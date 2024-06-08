package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.mappers.GameMapper;
import hu.bgy.pokerapp.models.Table;
import hu.bgy.pokerapp.repositories.TableRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {
    // private final TableServiceImpl<TexasHoldemRound, TexasHoldem> table;
    private final GameMapper gameMapper;
    private final TableRepo tableRepo;

    @Override
    public @NonNull TableDTO createGame(@NonNull final TableSetupDTO tableSetup) {
        final Table table = gameMapper.mapTableSetupToTable(tableSetup);
        return gameMapper.mapTableToTableDTO(tableRepo.save(table));
    }
}
