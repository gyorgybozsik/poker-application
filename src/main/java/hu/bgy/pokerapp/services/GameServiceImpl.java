package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.mappers.GameMapper;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {
   // private final TableServiceImpl<TexasHoldemRound, TexasHoldem> table;
    private final GameMapper gameMapper;

    @Override
    public @NonNull TableDTO createGame(@NonNull final TableSetupDTO tableSetup) {
        final Table table = gameMapper.mapTableSetupToTable(tableSetup);
        //TODO: Save table to DB
        return gameMapper.mapTableToTableDTO(table);
    }
}
