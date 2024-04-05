package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.PokerGameDTO;
import hu.bgy.pokerapp.dtos.PokerSetupDTO;
import hu.bgy.pokerapp.models.round.TexasHoldemRound;
import hu.bgy.pokerapp.services.poker.TexasHoldem;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
    private TableServiceImpl<TexasHoldemRound, TexasHoldem> table;

    @Override
    public @NonNull PokerGameDTO createGame(@NonNull PokerSetupDTO pokerSetup) {
        return null;
    }
}
