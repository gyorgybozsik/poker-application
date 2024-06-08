package hu.bgy.pokerapp.mappers;

import hu.bgy.pokerapp.dtos.BalanceDTO;
import hu.bgy.pokerapp.dtos.PlayerDTO;
import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.models.Balance;
import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class GameMapperImpl implements GameMapper {
    @Override
    public @NonNull Table mapTableSetupToTable(@NonNull final TableSetupDTO tableSetup) {
        final Table table = new Table(tableSetup.pokerType(), tableSetup.smallBlind());
        table.setSeats(mapPlayerDTOsTOPlayers(tableSetup.players(), tableSetup.cash()));

        return table;
    }

    private @NonNull List<Player> mapPlayerDTOsTOPlayers(
            @NonNull final List<PlayerDTO> playerDTOs,
            @NonNull final BigDecimal cash) {
        return playerDTOs.stream()
                .map(playerDTO -> mapPlayerDTOToPlayer(playerDTO, cash))
                .toList();
    }

    private @NonNull Player mapPlayerDTOToPlayer(
            @NonNull PlayerDTO playerDTO,
            @NonNull BigDecimal cash) {
        return new Player(playerDTO.name(), cash);
    }

    @Override
    public @NonNull TableDTO mapTableToTableDTO(@NonNull final Table table) {

        return new TableDTO(table.getId(),
                table.getSpeaker(),
                table.getPokerType(),
                table.getSmallBlind(),
                mapPlayersToPlayerDTOs(table.getSeats()));
    }

    private List<PlayerDTO> mapPlayersToPlayerDTOs(@NonNull final List<Player> seats) {
        return seats.stream()
                .map(this::mapPlayerToPlayerDTO)
                .toList();
    }

    private PlayerDTO mapPlayerToPlayerDTO(Player player) {
        return new PlayerDTO(player.getName(), mapBalanceToBalanceDTO(player.getBalance()));
    }

    private BalanceDTO mapBalanceToBalanceDTO(Balance balance) {
        //return new BalanceDTO(balance.getCash(), balance.getBet());
    return BalanceDTO.builder().cash(balance.getCash()).bet(balance.getBet()).build();
    }
}
