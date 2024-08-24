package hu.bgy.pokerapp.components;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TableValidator {
    public List<String> validateSpeakerAction(final @NonNull Table table,
                                              final @NonNull SpeakerActionDTO speakerActionDTO) {
        List<String> massages = new ArrayList<>();
        Player raiser = table.getLastRaiserPlayer();
        Player speaker = table.getSpeakerPlayer();
        Player player = table.getPlayer(speakerActionDTO.playerId());
        if (speaker != player) {
            massages.add("Speaker not equals with speaker action");
            return massages;
        }
        switch (speakerActionDTO.playerAction()) {
            case CHECK -> {
               //  if (raiser.getBalance().getBet().compareTo(speaker.getBalance().getBet()) != 0) {
               //     massages.add("checker should have same amount as raiser");
               //  }
            }
            case CALL -> {
                //  if (!(((raiser.getBalance().getBet().max(table.getBigBlind()).compareTo(speaker.getBalance().getBet().add(speakerActionDTO.changeAmount())) == 0 && table.getRound() == 0) ||
                //          (raiser.getBalance().getBet().compareTo(speaker.getBalance().getBet().add(speakerActionDTO.changeAmount())) == 0 && table.getRound() != 0)
                //  ) ||
                //          speakerActionDTO.changeAmount().compareTo(speaker.getBalance().getCash()) == 0)
                //  ) {
                //      massages.add("this caller is not a caller");
                //  }

            }

            case RAISE -> {
                // if (!(speakerActionDTO.changeAmount().compareTo(table.getBigBlind().max(raiser.getBalance().getBet().subtract(speaker.getBalance().getBet()))) < 0 ||
                //         (speakerActionDTO.changeAmount().compareTo(speaker.getBalance().getCash()) == 0 &&
                //                 raiser.getBalance().getBet().compareTo(speakerActionDTO.changeAmount().add(speaker.getBalance().getBet())) < 1))) {
                //     throw new IllegalStateException("not valid amount");
                // }

            }
            case FOLD -> {

            }
        }
        return massages;
    }
}
