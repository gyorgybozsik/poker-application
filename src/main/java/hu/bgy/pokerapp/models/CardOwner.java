package hu.bgy.pokerapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(CardOwner.CardOwnerId.class)
@jakarta.persistence.Table(name = "card_owners")
@Data
@EqualsAndHashCode
public class CardOwner {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardOwnerId implements Serializable {
        private UUID cardId;
        private UUID tableId;
        private UUID handId;
    }

    @Id
    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Id
    @Column(name = "table_id", nullable = true)
    private UUID tableId;

    @Id
    @Column(name = "hand_id")
    private UUID handId;

    @ManyToOne(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "card_id", insertable = false, updatable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "table_id", insertable = false, updatable = false)
    private Table table;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "hand_id", insertable = false, updatable = false)
    private Hand hand;
}
