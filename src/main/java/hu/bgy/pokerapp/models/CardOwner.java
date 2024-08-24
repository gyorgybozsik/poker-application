package hu.bgy.pokerapp.models;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "card_owners")
@Data
public class CardOwner {
    @Embeddable
    @Data
    public static class CardOwnerId implements Serializable {
        private UUID cardId;
        private UUID playerId;
        private UUID tableId;
    }

    @EmbeddedId
    private CardOwnerId id;

    @ManyToOne
    @MapsId("cardId")
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @MapsId("tableId")
    @JoinColumn(name = "table_id")
    private hu.bgy.pokerapp.models.Table table;
}
