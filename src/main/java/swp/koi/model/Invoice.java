package swp.koi.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.InvoiceStatusEnums;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Invoice", indexes = {
        @Index(name = "idx_invoice_member_id", columnList = "member_id"),
        @Index(name = "idx_invoice_invoiceDate", columnList = "invoiceDate"),
        @Index(name = "idx_invoice_status", columnList = "status"),
        @Index(name = "idx_invoice_fishId", columnList = "fishId"),
        // Add other indexes as needed
})
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer invoiceId;

    @Column(nullable = false)
    float finalAmount;

    @Column(nullable = false)
    java.time.LocalDateTime invoiceDate;

    float tax;

    LocalDateTime dueDate;

    float subTotal;

    @Min(value = 0, message = "distance must be > 0")
    Float kilometers;

    @Column(columnDefinition = "nvarchar(max)")
    String address;


    Float priceWithoutShipFee;

    @Enumerated(EnumType.STRING)
    InvoiceStatusEnums status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @Column(name = "paymentLink", length = 2048)

    String paymentLink;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Lot_id")
    Lot lot;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fishId")
    KoiFish koiFish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId")
    Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LRID")
    LotRegister lotRegister;
}
