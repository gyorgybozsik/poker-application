package hu.bgy.pokerapp.mappers;

import hu.bgy.pokerapp.dtos.PlayerDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameMapperImpl implements GameMapper {
    @Override
    public @NonNull Table mapTableSetupToTable(@NonNull final TableSetupDTO tableSetup) {
        final Table table = new Table(tableSetup.pokerType(), tableSetup.smallBlind());
        table.setSeats(mapPlayerDTOsTOPlayers(tableSetup.players(), tableSetup.cash()));

        return table;
    }

    private @NonNull Deque<Player> mapPlayerDTOsTOPlayers(
            @NonNull final List<PlayerDTO> playerDTOs,
            @NonNull final BigDecimal cash) {
        return playerDTOs.stream()
                .map(playerDTO -> mapPlayerDTOToPlayer(playerDTO, cash))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private @NonNull Player mapPlayerDTOToPlayer(
            @NonNull PlayerDTO playerDTO,
            @NonNull BigDecimal cash) {
        return new Player(playerDTO.name(), cash);
    }

    @Override
    public @NonNull TableDTO mapTableToTableDTO(@NonNull final Table table) {

        return new TableDTO(table.getUuid(),
                table.getPokerType(),
                table.getSmallBlind(),
                mapPlayersToPlayerDTOs(table.getSeats()));
    }

    private Deque<PlayerDTO> mapPlayersToPlayerDTOs(@NonNull final Deque<Player> seats) {
        return seats.stream()
                .map(this::mapPlayerToPlayerDTO)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private PlayerDTO mapPlayerToPlayerDTO(Player player) {
        return new PlayerDTO(player.getName());
    }
}
