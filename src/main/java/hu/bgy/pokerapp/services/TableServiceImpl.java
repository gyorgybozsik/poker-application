package hu.bgy.pokerapp.services;

import hu.bgy.pokerapp.dtos.SpeakerActionDTO;
import hu.bgy.pokerapp.enums.PokerType;
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
   final Set<Poker> pokerKinds ;

   private Poker getPokerKind(final @NonNull PokerType pokerType) {
      List<Poker> pokers = pokerKinds.stream().filter(poker -> poker.isPokerKind(pokerType)).toList();
      if (pokers.size()!=1) throw new IllegalStateException();
      return pokers.getFirst();
   }

   @Override
   public @NonNull Table performTableSpeaker(@NonNull Table table, @NonNull SpeakerActionDTO speakerActionDTO) {
       //todo ki kell szedni a playert
      //todo meg kell nézni a player-e a speaker speakeractiondto player id
      //todo update status és balance
      //todo ki kell számolni a következő roundot és speakert

      return null;
   }
}
