package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.koi.model.Lot;
import swp.koi.model.LotRegister;
import swp.koi.model.Member;
import swp.koi.model.enums.LotRegisterStatusEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotRegisterRepository extends JpaRepository<LotRegister, Integer> {

    Optional<List<LotRegister>> findAllByLot(Lot lot);

    LotRegister findLotRegisterByLotAndMember(Lot lot, Member member);

    LotRegister findByMember(Member member);

    List<LotRegister> findAllByMember(Member member);

    LotRegister findByLotAndStatus(Lot lot, LotRegisterStatusEnum status);

    List<LotRegister> findAllByStatus(LotRegisterStatusEnum status);
}
