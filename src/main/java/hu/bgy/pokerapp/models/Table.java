package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.PokerType;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.UUID;

@Data
public class Table {
    private UUID uuid;
    private PokerType pokerType;
    private BigDecimal smallBlind;
    private Deque<Player> seats;


    public Table(@NonNull final PokerType pokerType,
                 @NonNull final BigDecimal smallBlind) {
        this.pokerType = pokerType;
        this.smallBlind = smallBlind;
    }

    private void rotatePlayers() {
        final Player player = seats.pollFirst();
        seats.addLast(player);
    }
}
