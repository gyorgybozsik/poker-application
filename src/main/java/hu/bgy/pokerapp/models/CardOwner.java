package hu.bgy.pokerapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Entity
@jakarta.persistence.Table(name = "card_owners", uniqueConstraints = {@UniqueConstraint(columnNames = {"card_id", "table_id", "hand_id"})})
@Data
@EqualsAndHashCode
public class CardOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    //@EqualsAndHashCode.Exclude
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
