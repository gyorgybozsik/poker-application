package hu.bgy.pokerapp.dtos;

import hu.bgy.pokerapp.enums.PokerType;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.UUID;

public record TableDTO(UUID uuid,
                       PokerType pokerType,
                       BigDecimal smallBlind,
                       Deque<PlayerDTO> seats) {


}
