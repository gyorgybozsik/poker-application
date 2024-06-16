package hu.bgy.pokerapp.mappers;

import hu.bgy.pokerapp.dtos.TableDTO;
import hu.bgy.pokerapp.dtos.TableSetupDTO;
import hu.bgy.pokerapp.models.Table;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

public interface GameMapper {
    @NonNull Table mapTableSetupToTable(@NonNull final TableSetupDTO tableSetup);

    @NonNull TableDTO mapTableToTableDTO(@NonNull final Table table);

    Set<Long> mapTablesToIDs(List<Table> tables);
}
