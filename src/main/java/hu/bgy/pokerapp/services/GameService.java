package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.exceptions.ValidationException;
import lombok.NonNull;

import java.util.Set;
import java.util.UUID;

public interface GameService {
    @NonNull TableDTO createGame(@NonNull final TableSetupDTO tableSetup);

    Set<UUID> getAllPokerTable();

    TableDTO loadGame(UUID tableId);

    TableDTO performTableSpeaker(SpeakerActionDTO speakerActionDTO, UUID tableId) throws ValidationException;
}
