package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.koi.model.Bid;
import swp.koi.model.Lot;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

    Optional<List<Bid>> getBidByLot(Lot lot);

    Optional<Integer> countDistinctMemberByLot(Lot lot);
}
