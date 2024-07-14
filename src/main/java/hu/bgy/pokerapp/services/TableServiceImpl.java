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

        Player raiser = table.getLastRaiserPlayer();
        Player speaker = table.getSpeakerPlayer();
        Player player = table.getPlayer(speakerActionDTO.playerId());
        if (speaker != player) {
            throw new IllegalStateException("Speaker not equals with speaker action");
        }
        switch (speakerActionDTO.playerAction()) {
            case CHECK -> {
                if (raiser.getBalance().getBet().compareTo(speaker.getBalance().getBet()) != 0) {
                    throw new IllegalStateException("checker should have same amount as raiser");
                }
                nextSpeaker(table);
            }
            case CALL -> {
                if (!(((raiser.getBalance().getBet().max(table.getBigBlind()).compareTo(speaker.getBalance().getBet().add(speakerActionDTO.changeAmount())) == 0 && table.getRound() == 0) ||
                        (raiser.getBalance().getBet().compareTo(speaker.getBalance().getBet().add(speakerActionDTO.changeAmount())) == 0 && table.getRound() != 0)
                ) ||
                        speakerActionDTO.changeAmount().compareTo(speaker.getBalance().getCash()) == 0)
                ) {
                    throw new IllegalStateException("this caller is not a caller");
                }
                speaker.bet(speakerActionDTO.changeAmount());
                nextSpeaker(table);
            }
            case RAISE -> {
                if (!(speakerActionDTO.changeAmount().compareTo(table.getBigBlind().max(raiser.getBalance().getBet().subtract(speaker.getBalance().getBet()))) < 0 ||
                        (speakerActionDTO.changeAmount().compareTo(speaker.getBalance().getCash()) == 0 &&
                                raiser.getBalance().getBet().compareTo(speakerActionDTO.changeAmount().add(speaker.getBalance().getBet())) < 1))) {
                    throw new IllegalStateException("not valid amount");
                }
                speaker.bet(speakerActionDTO.changeAmount());
                table.setAfterLast(speaker.getState().getRoundRole());
                nextSpeaker(table);
            }
            case FOLD -> {
                player.fold();
                nextSpeaker(table);
            }
        }


        //todo update status és balance
        //todo ki kell számolni a következő roundot és speakert

        return null;
    }


    public RoundRole nextSpeaker(final @NonNull Table table) {
        RoundRole[] roundRoles = RoundRole.values();
        //todo meg kell csinálni a körbemenetelt és h az afterlast speakerig kell mennie

        for (int i = table.getSpeaker().ordinal() + 1; i < roundRoles.length; i++) {
            RoundRole roundRole = roundRoles[i];
            if (table.getSeats().stream().anyMatch(player -> player.isSpeakable(roundRole))) {
                return roundRole;
            }
        }
        return null;
    }
}
