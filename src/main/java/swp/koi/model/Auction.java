package swp.koi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.type.DateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;
import swp.koi.model.enums.AuctionStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Auction", indexes = {
        @Index(name = "idx_auction_start_time", columnList = "startTime"),
        @Index(name = "idx_auction_end_time", columnList = "endTime"),
        @Index(name = "idx_auction_status", columnList = "status")
})
@AllArgsConstructor
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer auctionId;

    @Column(nullable = false)
    LocalDateTime startTime;

    @Column(nullable = false)
    LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AuctionStatusEnum status;

    @OneToMany(mappedBy = "auction", fetch = FetchType.LAZY)
            @JsonManagedReference
    List<Lot> lots;

    public Auction() {
    }

}
