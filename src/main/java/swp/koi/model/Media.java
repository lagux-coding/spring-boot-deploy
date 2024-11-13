package swp.koi.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer mediaId;

    @Column(nullable = false)
    String imageUrl;

    @Column(nullable = false)
    String videoUrl;

    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    KoiFish koiFish;

    public Media() {
    }

    public Media(String imageUrl, String videoUrl, KoiFish koiFish) {
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.koiFish = koiFish;
    }
}
