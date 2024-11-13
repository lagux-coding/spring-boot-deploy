package swp.koi.service.memberService;

import swp.koi.model.Account;
import swp.koi.model.Member;

import java.util.List;

public interface MemberService {

    Member getMemberById(int id);

    void createMember(Account account);

    Integer getMemberIdByAccount(Account account);

    Member getMemberByAccount(Account account);

    List<Member> getAllMembersToRefund();
}
