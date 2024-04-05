package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.PokerGameDTO;
import hu.bgy.pokerapp.dtos.PokerSetupDTO;
import lombok.NonNull;

public interface GameService {
    @NonNull PokerGameDTO createGame(@NonNull final PokerSetupDTO pokerSetup);
}
