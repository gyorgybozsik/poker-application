package hu.bgy.pokerapp.models;

import hu.bgy.pokerapp.enums.PokerType;
import hu.bgy.pokerapp.enums.RoundRole;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static hu.bgy.pokerapp.enums.RoundRole.SMALL_BLIND;
import static hu.bgy.pokerapp.enums.RoundRole.SPEAKER_1;


@Data
@Entity
@jakarta.persistence.Table(name = "tables")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // @SequenceGenerator(name = "t_seq_g", sequenceName = "tables_id_seq")
    private UUID id;

    @Column(name = "round", nullable = false)
    private int round;

    @Enumerated(EnumType.STRING)
    @Column(name = "poker_type", nullable = false)
    private PokerType pokerType;

    @Column(name = "small_blind", nullable = false)
    private BigDecimal smallBlind;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<Player> seats;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaker", nullable = false)
    private RoundRole speaker;

    @Enumerated(EnumType.STRING)
    @Column(name = "after_last", nullable = false)
    private RoundRole afterLast;

    public void setCards(Set<CardOwner> cards) {
        this.cards = cards;
    }

    @OneToMany (mappedBy = "table")
 //   @JoinTable(
 //           name = "card_owners",
 //           joinColumns = @JoinColumn(name = "table_id"),
 //           inverseJoinColumns = @JoinColumn(name = "card_id")
 //   )
    private Set<CardOwner> cards = new HashSet<>();

    public void setSeats(List<Player> seats) {
        this.seats = seats;
        if (seats.size() == 2) {
            speaker = RoundRole.SMALL_BLIND;
            afterLast = SMALL_BLIND;
        } else {
            speaker = SPEAKER_1;
            afterLast = SPEAKER_1;
        }

    }

    public Table(@NonNull final PokerType pokerType,
                 @NonNull final BigDecimal smallBlind) {
        this.pokerType = pokerType;
        this.smallBlind = smallBlind;
    }

    public BigDecimal getBigBlind() {
        return smallBlind.multiply(BigDecimal.TWO);
    }

    public Player getSpeakerPlayer() {
        return seats
                .stream()
                .filter(player1 -> player1.getState().getRoundRole() == speaker)
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    public Player getLastRaiserPlayer() {
        return seats
                .stream()
                .filter(player1 -> player1.getState().getRoundRole() == afterLast)
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    public Player getPlayer(final UUID id) {
        return seats
                .stream()
                .filter(player1 -> player1.getId().equals(id))
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    public Set<Card> getCardsForDeck() {
        return cards.stream().map(CardOwner::getCard).collect(Collectors.toSet());
    }
   // public Set<Card> getCards() {
   //     return cards.stream().map(CardOwner::getCard).collect(Collectors.toSet());
   // }

    public List<Player> getActivePlayers() {
        return seats.stream().filter(Player::isActive).toList();
    }
}
//todo long ID --> uuid
// adatbázis script serial heylet uuid,
// UUid generálás megvalósítása
// postgesql uuid generálás<-keresés

