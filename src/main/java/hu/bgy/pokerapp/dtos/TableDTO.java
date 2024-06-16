package hu.bgy.pokerapp.dtos;

import hu.bgy.pokerapp.enums.PokerType;
import hu.bgy.pokerapp.enums.RoundRole;

import java.math.BigDecimal;
import java.util.List;

public record TableDTO(//UUID uuid,
                       long id,
                       int round,
                       RoundRole speaker,
                       PokerType pokerType,
                       BigDecimal smallBlind,
                       List<PlayerDTO> seats) {


}
