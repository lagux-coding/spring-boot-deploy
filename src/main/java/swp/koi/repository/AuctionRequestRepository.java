package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.koi.model.Account;
import swp.koi.model.AuctionRequest;
import swp.koi.model.enums.AuctionRequestStatusEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Integer>{

    List<AuctionRequest> findAllByStatus(AuctionRequestStatusEnum status);

    Optional<AuctionRequest> findByRequestId(Integer requestId);

    @Query("SELECT ar FROM AuctionRequest ar WHERE ar.account.id = :accountId")
    List<AuctionRequest> findAllRequestByAccountId(@Param("accountId") Integer accountId);

    @Query("SELECT ar FROM AuctionRequest ar WHERE ar.koiBreeder.id = :breederId")
    List<AuctionRequest> findAllByBreederId(Integer breederId);

}
