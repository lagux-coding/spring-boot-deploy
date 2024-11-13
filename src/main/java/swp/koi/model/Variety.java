package swp.koi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Variety")
public class Variety {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer varietyId;

    @Column(nullable = false)
    String varietyName;

    @OneToMany(mappedBy = "variety",fetch = FetchType.LAZY)
    List<KoiFish> koiFishes;

    public Variety() {
    }

    public Variety(String varietyName, List<KoiFish> koiFishes) {
        this.varietyName = varietyName;
        this.koiFishes = koiFishes;
    }
}
