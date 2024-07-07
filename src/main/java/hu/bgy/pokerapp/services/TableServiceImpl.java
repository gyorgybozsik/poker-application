package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.enums.PokerType;
import hu.bgy.pokerapp.enums.RoundRole;
import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.Table;
import hu.bgy.pokerapp.models.poker.Poker;
import hu.bgy.pokerapp.services.poker.TableService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TableServiceImpl implements TableService {
    final Set<Poker> pokerKinds;

    private Poker getPokerKind(final @NonNull PokerType pokerType) {
        List<Poker> pokers = pokerKinds.stream().filter(poker -> poker.isPokerKind(pokerType)).toList();
        if (pokers.size() != 1) throw new IllegalStateException();
        return pokers.getFirst();
    }

    @Override
    public @NonNull Table performTableSpeaker(@NonNull Table table, @NonNull SpeakerActionDTO speakerActionDTO) {
        //todo később validáld le hogya  tableban lévő player megeggyezik e a speakeractionben lévő playyerrel
        //todo meg kell nézni a player-e a speaker speakeractiondto player id
        Player player = table
                .getSeats()
                .stream()
                .filter(player1 -> player1.getId().equals(speakerActionDTO.playerId()))
                .findFirst().orElseThrow(IllegalStateException::new);
        switch (speakerActionDTO.playerAction()) {
            case CHECK ->
            case CALL ->
            case RAISE ->
            case FOLD -> {player.fold(); nextSpeaker(table);
            }
        }


        //todo update status és balance
        //todo ki kell számolni a következő roundot és speakert

        return null;
    }

    public RoundRole nextSpeaker(final @NonNull Table table) {
        RoundRole[] roundRoles = RoundRole.values();

        for (int i = table.getSpeaker().ordinal() + 1; i < roundRoles.length; i++) {
            RoundRole roundRole = roundRoles[i];
            if (table.getSeats().stream().anyMatch(player -> player.getState().isActiveRoundRole(roundRole))) {
                return roundRole;
            }
        }

    }
}
