package swp.koi.service.koiBreederService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import swp.koi.dto.request.AccountRegisterDto;
import swp.koi.dto.request.KoiBreederDto;
import swp.koi.dto.request.UpdateBreederProfileDto;
import swp.koi.dto.response.KoiBreederResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.Account;
import swp.koi.model.KoiBreeder;
import swp.koi.model.enums.AccountRoleEnum;
import swp.koi.repository.KoiBreederRepository;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.authService.GetUserInfoByUsingAuth;

@Service
@RequiredArgsConstructor
public class KoiBreederServiceImpl implements KoiBreederService{

    private final KoiBreederRepository koiBreederRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GetUserInfoByUsingAuth getUserInfoByUsingAuth;

    @Override
    public KoiBreederResponseDto createKoiBreeder(@Valid KoiBreederDto request) throws KoiException{
        try{
            KoiBreeder koiBreeder = new KoiBreeder();
            koiBreeder.setBreederName(request.getBreederName());
            koiBreeder.setLocation(request.getLocation());
            koiBreeder.setStatus(true);

            AccountRegisterDto accountRegisterDTO = request.getAccount();
            Account account = new Account();
            modelMapper.map(accountRegisterDTO, account);

            account.setPassword(passwordEncoder.encode(accountRegisterDTO.getPassword()));
            account.setRole(AccountRoleEnum.BREEDER);
            account.setStatus(true);

            accountService.createAccount(account);

            koiBreeder.setAccount(account);

            KoiBreederResponseDto breederResponse = modelMapper.map(koiBreederRepository.save(koiBreeder), KoiBreederResponseDto.class);

            return breederResponse;
        } catch (KoiException e) {
            throw e;
        }
    }

    @Override
    public KoiBreeder findByAccount(Account account) {
        return koiBreederRepository.findByAccount(account).orElseThrow(() -> new KoiException(ResponseCode.BREEDER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void updateBreederProfile(UpdateBreederProfileDto request) {
        Account account = getUserInfoByUsingAuth.getAccountFromAuth();
        KoiBreeder koiBreeder = findByAccount(account);
        if(koiBreeder == null)
            throw new KoiException(ResponseCode.BREEDER_NOT_FOUND);

        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setPhoneNumber(request.getPhoneNumber());

        accountService.saveAccount(account);

        koiBreeder.setBreederName(request.getBreederName());
        koiBreeder.setLocation(request.getLocation());

        koiBreederRepository.save(koiBreeder);
    }

    @Override
    public KoiBreeder getBreederInfo() {
        Account account = getUserInfoByUsingAuth.getAccountFromAuth();
        if(!account.getRole().equals(AccountRoleEnum.BREEDER))
            throw new KoiException(ResponseCode.BREEDER_NOT_FOUND);
        return account.getKoiBreeder();
    }
}
