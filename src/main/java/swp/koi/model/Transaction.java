package swp.koi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.TransactionTypeEnum;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "[Transaction]", indexes = {
        @Index(name = "idx_transaction_lot_id", columnList = "lot_id"),
        @Index(name = "idx_transaction_member_id", columnList = "member_id"),
        @Index(name = "idx_transaction_breeder_id", columnList = "breeder_id"),
        @Index(name = "idx_transaction_date", columnList = "transactionDate"),
        @Index(name = "idx_transaction_type", columnList = "transactionType")
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;


    @Column(name = "transactionDate", updatable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private String paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breeder_id")
    private KoiBreeder breeder;

    @OneToOne(mappedBy = "transaction", fetch = FetchType.LAZY)
    private Invoice invoice;
}
