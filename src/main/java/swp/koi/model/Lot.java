package swp.koi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import swp.koi.model.enums.AuctionTypeNameEnum;
import swp.koi.model.enums.LotStatusEnum;

import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Lot", indexes = {
        @Index(name = "idx_lot_auction_id", columnList = "auctionId"),
        @Index(name = "idx_lot_starting_time", columnList = "startingTime"),
        @Index(name = "idx_lot_ending_time", columnList = "endingTime"),
        @Index(name = "idx_lot_status", columnList = "status"),
        @Index(name = "idx_lot_current_member_id", columnList = "currentMemberId")
})
@AllArgsConstructor
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer lotId;

    @Column(nullable = false)
    float deposit;

    @Column(nullable = false)
    float startingPrice;

    @Column(nullable = false)
    java.time.LocalDateTime startingTime;

    @Column(nullable = false)
    java.time.LocalDateTime endingTime;

    @Column(nullable = false)
    float increment;

    @Column(columnDefinition = "integer null")
    Integer currentMemberId;

    float currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    LotStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    KoiFish koiFish;

    @OneToMany(mappedBy = "lot", fetch = FetchType.LAZY)
    List<LotRegister> lotRegisters;

    @OneToMany(mappedBy = "lot",fetch = FetchType.LAZY)
    List<Bid> bids;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auctionId")
    @JsonBackReference
    Auction auction;

    @ManyToOne()
    @JoinColumn(name = "auctionTypeId")
    AuctionType auctionType;

    @OneToMany(mappedBy = "lot", fetch = FetchType.LAZY)
    List<Transaction> transactions;

    @OneToOne(mappedBy = "lot", fetch = FetchType.LAZY)
    Invoice invoice;

    public Lot() {
    }


}
