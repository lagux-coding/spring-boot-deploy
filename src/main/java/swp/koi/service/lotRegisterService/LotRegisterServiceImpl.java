package swp.koi.service.lotRegisterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import swp.koi.dto.request.LotRegisterDto;
import swp.koi.dto.response.LotRegisterResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.*;
import swp.koi.model.enums.LotRegisterStatusEnum;
import swp.koi.model.enums.TransactionTypeEnum;
import swp.koi.repository.AccountRepository;
import swp.koi.repository.LotRegisterRepository;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.authService.GetUserInfoByUsingAuth;
import swp.koi.service.lotService.LotService;
import swp.koi.service.memberService.MemberService;
import swp.koi.service.memberService.MemberServiceImpl;
import swp.koi.service.transactionService.TransactionService;
import swp.koi.service.vnPayService.VnpayService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LotRegisterServiceImpl implements LotRegisterService{

    private final LotRegisterRepository lotRegisterRepository;
    private final LotService lotService;
    private final MemberServiceImpl memberServiceImpl;
    private final ModelMapper modelMapper;
    private final VnpayService vnpayService;
    private final AccountRepository accountRepository;
    private final GetUserInfoByUsingAuth getUserInfoByUsingAuth;
    private final MemberService memberService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    /**
     * @param lotRegisDto
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String regisSlotWithLotId(LotRegisterDto lotRegisDto) throws UnsupportedEncodingException, KoiException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Member member = memberServiceImpl.getMemberByAccount(accountRepository.findByEmail(username)
        .orElseThrow(() -> new KoiException(ResponseCode.MEMBER_NOT_FOUND)));

        var isLotEnded = lotEndedYet(lotRegisDto.getLotId());

        if(isLotEnded){
            throw new KoiException(ResponseCode.LOT_BIDTIME_PASSED);
        }

        // Validate if the member is already registered for this lot
        var isUserRegistered = validateMemberRegistration(lotRegisDto.getLotId(), member);

        if(isUserRegistered) {
            throw new KoiException((ResponseCode.MEMBER_ALREADY_REGISTERED));
        }
        return vnpayService.generateInvoice(lotRegisDto.getLotId(),member.getMemberId(), TransactionTypeEnum.DEPOSIT);

    }

    private boolean lotEndedYet(int lotId) {
        Lot lot = lotService.findLotById(lotId);
        return lot.getEndingTime().isBefore(LocalDateTime.now());
    }

    @Override
    public List<LotRegisterResponseDto> listLotRegistersByLotId(int lotId) throws KoiException {
        Lot lot = lotService.findLotById(lotId);

        List<LotRegister> lotRegisters = lotRegisterRepository.findAllByLot(lot).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        return lotRegisters
                .stream()
                .map(lotRegister -> modelMapper.map(lotRegister, LotRegisterResponseDto.class)).collect(Collectors.toList());
    }

    private boolean validateMemberRegistration(Integer lotId, Member member){

        Lot lot = lotService.findLotById(lotId);
        List<LotRegister> lotRegisters = lotRegisterRepository.findAllByLot(lot).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        return lotRegisters.stream().anyMatch(lr -> lr.getMember().getMemberId().equals(member.getMemberId()));

    }


    @Override
    public LotRegister getLotWinner(Integer lotId) {
        Lot lot = lotService.findLotById(lotId);

        List<LotRegister> lotRegisters = lotRegisterRepository.findAllByLot(lot).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        for(LotRegister lotRegis : lotRegisters){
            if(lotRegis.getStatus().equals(LotRegisterStatusEnum.WON)){
                return lotRegis;
            }
        }
        return null;
    }

    @Override
    public boolean isRegistered(Integer lotId, Integer accountId) {
        Lot lot = lotService.findLotById(lotId);
        Account account = accountService.findById(accountId);
        Member member = memberServiceImpl.getMemberByAccount(account);

        return lotRegisterRepository.findLotRegisterByLotAndMember(lot, member) != null;
    }

    @Override
    public List<LotRegisterResponseDto> findAllLotRegisWithStatus(LotRegisterStatusEnum status){
        List<LotRegister> refundList = lotRegisterRepository.findAllByStatus(status);
        return refundList
                .stream()
                .map(lotRegister -> modelMapper.map(lotRegister, LotRegisterResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LotRegister> getAllDepositedLotForMember(Integer accountId) {
        Account account = accountService.findById(accountId);
        Member member = memberService.getMemberByAccount(account);
        if(member == null)
            throw new KoiException(ResponseCode.MEMBER_NOT_FOUND);

        return lotRegisterRepository.findAllByMember(member);
    }

    @Override
    public void refundForMember(int lotRegisterId){
        LotRegister lotRegister = lotRegisterRepository.findById(lotRegisterId)
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_REGISTER_NOT_FOUND));

        lotRegister.setStatus(LotRegisterStatusEnum.REFUNDED);
        lotRegisterRepository.save(lotRegister);

        transactionService.createTransactionForRefund(lotRegister);
    }
}
