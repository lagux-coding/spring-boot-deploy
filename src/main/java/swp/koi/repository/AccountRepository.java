package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.koi.dto.request.AccountRegisterDto;
import swp.koi.model.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<AccountRegisterDto> findByAccountId(Integer accountId);
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
}
