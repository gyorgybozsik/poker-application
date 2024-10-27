package hu.bgy.pokerapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId")
    @ToString.Exclude
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tableId")
    @ToString.Exclude
    @JoinColumn(name = "table_id")
    private hu.bgy.pokerapp.models.Table table;
}
