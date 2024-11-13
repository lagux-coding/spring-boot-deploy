package swp.koi.service.memberService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.Account;
import swp.koi.model.LotRegister;
import swp.koi.model.Member;
import swp.koi.model.enums.AccountRoleEnum;
import swp.koi.model.enums.LotRegisterStatusEnum;
import swp.koi.repository.LotRegisterRepository;
import swp.koi.repository.LotRepository;
import swp.koi.repository.MemberRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final LotRegisterRepository lotRegisterRepository;

    @Override
    public Member getMemberById(int id) {
        return memberRepository.findById(id).orElseThrow(() -> new KoiException(ResponseCode.MEMBER_NOT_FOUND));
    }

    @Override
    public void createMember(Account account) {
        if(account.getRole() == AccountRoleEnum.MEMBER){
            Member member = Member.builder()
                    .account(account)
                    .build();
            memberRepository.save(member);
        }
    }

    @Override
    public Integer getMemberIdByAccount(Account account) {

        var memberId = memberRepository.findByAccount(account);

        return memberId.getMemberId();
    }

    @Override
    public Member getMemberByAccount(Account account) {

        return memberRepository.findByAccount(account);

    }

    @Override
    public List<Member> getAllMembersToRefund() {
        List<LotRegister> lotRegisters = lotRegisterRepository.findAllByStatus(LotRegisterStatusEnum.LOSE);
        return memberRepository.findAllByLotRegisters(lotRegisters);
    }
}
