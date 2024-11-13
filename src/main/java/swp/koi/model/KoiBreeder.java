package swp.koi.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "KoiBreeder")
@Builder
@AllArgsConstructor
public class KoiBreeder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer breederId;

    @Column(nullable = false)
    String breederName;

    @Column(nullable = false)
    String location;

    @Column(nullable = false)
    boolean status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    @OneToMany(mappedBy = "koiBreeder", fetch = FetchType.LAZY)
    List<AuctionRequest> auctionRequests;

    @OneToMany(mappedBy = "breeder",fetch = FetchType.LAZY)
    List<Transaction> transactions;

    public KoiBreeder() {
    }

}
