package swp.koi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.AccountRoleEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Account")
@Builder
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer accountId;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    String firstName;

    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    String lastName;

    @Column(nullable = false)
    String password;

    String phoneNumber;

    @Enumerated(EnumType.STRING)
    AccountRoleEnum role;

    @Column(nullable = false)
    boolean status;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Member member;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    KoiBreeder koiBreeder;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    List<AuctionRequest> auctionRequest;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    List<Invoice> invoices;

    @Column(name = "createAt", updatable = false)
    LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }

    public Account() {
    }


}



