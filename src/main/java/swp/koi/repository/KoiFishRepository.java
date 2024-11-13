package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.koi.model.KoiFish;

import java.util.Optional;

@Repository
public interface KoiFishRepository extends JpaRepository<KoiFish, Integer> {
    Optional<KoiFish> findByFishId(Integer fishId);
}
