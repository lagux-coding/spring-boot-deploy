package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.koi.model.Invoice;
import swp.koi.model.Lot;
import swp.koi.model.Member;
import swp.koi.model.Transaction;
import swp.koi.model.enums.TransactionTypeEnum;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    boolean existsByTransactionTypeAndLotAndMember(TransactionTypeEnum transactionTypeEnum, Lot lot, Member member);


}
