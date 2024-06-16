package hu.bgy.pokerapp.services.poker;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;

public interface TableService {
    @NonNull Table performTableSpeaker(final @NonNull Table table,
                                       final @NonNull SpeakerActionDTO speakerActionDTO);

}
