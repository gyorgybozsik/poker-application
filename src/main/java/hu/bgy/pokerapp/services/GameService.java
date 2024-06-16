package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import lombok.NonNull;

import java.util.Set;

public interface GameService {
    @NonNull TableDTO createGame(@NonNull final TableSetupDTO tableSetup);

    Set<Long> getAllPokerTable();

    TableDTO loadGame(long tableId);
}
