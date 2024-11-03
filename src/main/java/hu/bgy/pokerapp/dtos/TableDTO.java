package hu.bgy.pokerapp.dtos;

import hu.bgy.pokerapp.enums.PokerType;
import hu.bgy.pokerapp.enums.RoundRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record TableDTO(//UUID uuid,
                       java.util.UUID id,
                       int round,
                       RoundRole speaker,
                       Set<CardDTO> cards,
                       PokerType pokerType,
                       BigDecimal smallBlind,
                       List<PlayerDTO> seats) {


}
