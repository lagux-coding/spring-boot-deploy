package swp.koi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.AuctionRequestStatusEnum;
import swp.koi.model.enums.AuctionTypeNameEnum;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "AuctionRequest")
public class AuctionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer requestId;

    @Column(name = "requestedAt", updatable = false)
    LocalDateTime requestedAt;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AuctionRequestStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "accountId")
    Account account;

    float offerPrice;

    @ManyToOne
    @JoinColumn(name = "auctionTypeId")
    AuctionType auctionType;

    @ManyToOne
    @JoinColumn(name = "breederId")
    KoiBreeder koiBreeder;

    Float auctionFinalPrice;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fishId")
    KoiFish koiFish;

    public AuctionRequest() {
    }

    public AuctionRequest(AuctionRequestStatusEnum status, Account account, KoiBreeder koiBreeder, KoiFish koiFish) {
        this.status = status;
        this.account = account;
        this.koiBreeder = koiBreeder;
        this.koiFish = koiFish;
    }
}
