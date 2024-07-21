package hu.bgy.pokerapp.mappers;

import hu.bgy.pokerapp.dtos.*;
import hu.bgy.pokerapp.enums.RoundRole;
import hu.bgy.pokerapp.models.Balance;
import hu.bgy.pokerapp.models.Player;
import hu.bgy.pokerapp.models.PlayerState;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GameMapperImpl implements GameMapper {
    @Override
    public @NonNull Table mapTableSetupToTable(@NonNull final TableSetupDTO tableSetup) {
        final Table table = new Table(tableSetup.pokerType(), tableSetup.smallBlind());
        table.setSeats(mapPlayerDTOsTOPlayers(tableSetup.players(), table, tableSetup.cash()));

        return table;
    }

    private @NonNull List<Player> mapPlayerDTOsTOPlayers(
            @NonNull final List<PlayerDTO> playerDTOs,
            @NonNull final Table table,
            @NonNull final BigDecimal cash) {
        List<Player> list = new ArrayList<>();
        RoundRole[] roundRoles = RoundRole.values();
        for (int i = 0; i < playerDTOs.size(); i++) {
            PlayerDTO playerDTO = playerDTOs.get(i);
            Player player = mapPlayerDTOToPlayer(playerDTO, cash, roundRoles[i]);
            player.setTable(table);
            list.add(player);
        }
        return list;
    }

    private @NonNull Player mapPlayerDTOToPlayer(
            @NonNull PlayerDTO playerDTO,
            @NonNull BigDecimal cash,
            @NonNull RoundRole roundRole
    ) {
        return new Player(playerDTO.name(), cash, roundRole);
    }

    @Override
    public @NonNull TableDTO mapTableToTableDTO(@NonNull final Table table) {

        return new TableDTO(table.getId(),
                table.getRound(),
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
        return new PlayerDTO(
                player.getId(),
                player.getName(),
                mapBalanceToBalanceDTO(player.getBalance()),
                mapPlayerStateToPlayerStateDTO(player.getState()));
    }

    private PlayerStateDTO mapPlayerStateToPlayerStateDTO(PlayerState playerState) {
        return PlayerStateDTO.builder().inGameState(playerState.getInGameState()).roundRole(playerState.getRoundRole()).build();
    }

    private BalanceDTO mapBalanceToBalanceDTO(Balance balance) {
        //return new BalanceDTO(balance.getCash(), balance.getBet());
        return BalanceDTO.builder().cash(balance.getCash()).bet(balance.getBet()).build();
    }

    @Override
    public Set<UUID> mapTablesToIDs(final @NonNull List<Table> tables) {
        return tables.stream().map(Table::getId).collect(Collectors.toSet());
    }
}
