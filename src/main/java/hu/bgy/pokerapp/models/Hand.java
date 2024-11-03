package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.Value;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Entity
@Table(name = "hands")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class Hand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "hand", cascade = CascadeType.ALL)
    private Set<CardOwner> cardOwners = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;
    @Transient
    private Value handScore;
    @Transient
    private HandEvaluation latestHandEvaluation;

    public Hand(final TreeSet<CardOwner> cardOwners) {
        this.cardOwners = cardOwners;
        // validate();
    }

    public void addCards(Set<CardOwner> cardOwner) {
        cardOwners.addAll(cardOwner);
    }

    public void addCard(Card card) {
        CardOwner cardOwner = new CardOwner();



        cardOwner.setCard(card);
        cardOwner.setHand(this);
        cardOwner.setTable(null);
        cardOwners.add(cardOwner);
    }

    public void throwCard(Set<Card> cards) {
        cardOwners.removeIf(cardOwner -> !cards.contains(cardOwner.getCard()));
    }

    public void validate() {
        if (isEmpty(cardOwners) || cardOwners.size() < 5) {
            throw new IllegalArgumentException();
        }
    }

    public HandState getHandState() {
        if (latestHandEvaluation == null) {
            throw new IllegalStateException("Latest hand evaluation is not initialized");
        }
        return latestHandEvaluation.getHandState();
    }

    public TreeSet<Card> getCards() {
        return cardOwners.stream().map(CardOwner::getCard).collect(Collectors.toCollection(TreeSet::new));
    }
}
