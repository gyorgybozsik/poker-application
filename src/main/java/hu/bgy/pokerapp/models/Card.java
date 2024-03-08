package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Rank;
import hu.bgy.pokerapp.enums.Symbol;

public record Card(Symbol symbol,
                   Rank rank) {
}
