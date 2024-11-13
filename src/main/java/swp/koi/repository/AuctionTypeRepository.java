package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.koi.model.AuctionType;
import swp.koi.model.enums.AuctionTypeNameEnum;

import java.util.Optional;

@Repository
public interface AuctionTypeRepository extends JpaRepository<AuctionType, Integer> {
    Optional<AuctionType> findByAuctionTypeName(@Param("name") AuctionTypeNameEnum name);
}
