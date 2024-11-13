package swp.koi.service.authService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.Account;
import swp.koi.model.Member;
import swp.koi.repository.AccountRepository;
import swp.koi.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class GetUserInfoByUsingAuth {

    private final MemberRepository MemberRepository;
    private final AccountRepository auctionRepository;

    public String getGmailFromAuth(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public Member getMemberFromAuth(){

        Account account = auctionRepository.findByEmail(getGmailFromAuth()).orElseThrow(() -> new KoiException(ResponseCode.ACCOUNT_NOT_FOUND));

        return MemberRepository.findByAccount(account);
    }

    public Account getAccountFromAuth(){

        return auctionRepository.findByEmail(getGmailFromAuth()).orElseThrow(() -> new KoiException(ResponseCode.ACCOUNT_NOT_FOUND));

    }

}
