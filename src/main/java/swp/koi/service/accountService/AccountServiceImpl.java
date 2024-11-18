package swp.koi.service.accountService;

import ch.qos.logback.core.util.StringUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import swp.koi.convert.AccountEntityToDtoConverter;
import swp.koi.dto.request.*;
import swp.koi.dto.response.AuthenticateResponse;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.Account;
import swp.koi.model.enums.AccountRoleEnum;
import swp.koi.model.enums.TokenType;
import swp.koi.repository.AccountRepository;
import swp.koi.service.authService.GetUserInfoByUsingAuth;
import swp.koi.service.jwtService.JwtServiceImpl;
import swp.koi.service.mailService.EmailContent;
import swp.koi.service.mailService.EmailService;
import swp.koi.service.memberService.MemberServiceImpl;
import swp.koi.service.redisService.RedisServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MemberServiceImpl memberService;
    private final JwtServiceImpl jwtService;
    private final AccountDetailService accountDetailService;
    private final AccountEntityToDtoConverter accountEntityToDtoConverter;
    private final RedisServiceImpl redisService;
    private final EmailService emailService;
    private final EmailContent emailContent;
    private final GetUserInfoByUsingAuth getUserInfoByUsingAuth;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Override
    public AccountRegisterDto findByAccountId(Integer accountId) {
        return accountRepository.findByAccountId(accountId).orElseThrow(() -> new KoiException(ResponseCode.ACCOUNT_ID_NOT_FOUND));
    }

    @Override
    public Account findById(Integer accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new KoiException(ResponseCode.ACCOUNT_ID_NOT_FOUND));
    }

    @Override
    public Account createAccount(Account account) throws KoiException{
        if(accountRepository.existsByEmail(account.getEmail()))
            throw new KoiException(ResponseCode.EMAIL_ALREADY_EXISTS);
        else
            return accountRepository.save(account);
    }

    @Override
    public Account createAccountByRequest(@Valid AccountRegisterDto request) throws KoiException{
        Account account = new Account();

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new KoiException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }

        modelMapper.map(request, account);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(AccountRoleEnum.MEMBER);
        account.setStatus(true);
        accountRepository.save(account);

        memberService.createMember(account);

        return account;
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new KoiException(ResponseCode.EMAIL_NOT_FOUND));
    }

    @Override
    public AuthenticateResponse login(AccountLoginDto request) throws KoiException {
        AuthenticateResponse authenticateResponse;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            var account = findByEmail(request.getEmail());

            String accessToken = jwtService.generateToken(account.getEmail(), TokenType.ACCESS_TOKEN);

            String refreshToken = jwtService.generateRefreshToken(account.getEmail(), TokenType.REFRESH_TOKEN);

            authenticateResponse = AuthenticateResponse.builder()
                    .account(accountEntityToDtoConverter.convertAccount(account))
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (BadCredentialsException e) {
            throw new KoiException(ResponseCode.INVALID_CREDENTIALS);
        } catch (DisabledException e){
            throw new KoiException(ResponseCode.ACCOUNT_DISABLED);
        }

        return authenticateResponse;
    }

    @Override
    public AuthenticateResponse loginGoogle(GoogleTokenRequestDto googleTokenDto) {
        String token = googleTokenDto.getToken();
        DecodedJWT decodedJWT = jwtService.verifyToken(token);
        if(decodedJWT == null)
            throw new KoiException(ResponseCode.INVALID_TOKEN);

        String email = decodedJWT.getClaim("email").asString();
        String givenName = decodedJWT.getClaim("given_name").asString();
        String familyName = decodedJWT.getClaim("family_name").asString();

        Account account = accountRepository.findByEmail(email).orElseGet(() -> {
            String randomPassword = generateRandomPassword();
            Account newAccount = Account.builder()
                    .email(email)
                    .firstName(givenName)
                    .lastName(familyName)
                    .password(passwordEncoder.encode(randomPassword))
                    .role(AccountRoleEnum.MEMBER)
                    .status(true)
                    .build();
            emailService.sendEmail(email, "Your account information", emailContent.createEmailSignUpGoogle(givenName, email, randomPassword));
            accountRepository.save(newAccount);
            memberService.createMember(newAccount);
            return newAccount;
        });

        AuthenticateResponse authenticateResponse;

        String accessToken = jwtService.generateToken(account.getEmail(), TokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.generateRefreshToken(account.getEmail(), TokenType.REFRESH_TOKEN);

        return authenticateResponse = AuthenticateResponse.builder()
                .account(accountEntityToDtoConverter.convertAccount(account))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateRandomPassword() {
        return RandomStringUtils.random(6, true, true);
    }

    /**
     * invalid old token when it come with the new refresh token
     * @param request
     * @return
     * @throws KoiException
     */
    @Override
    public AuthenticateResponse refreshToken(HttpServletRequest request) throws KoiException {
        String accessToken = null;

        //fetch token
        String refreshToken = request.getHeader("x-token");

        if (StringUtil.isNullOrEmpty(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is empty");
        }


        String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

        //initialize account
        var userDetails = accountDetailService.loadUserByUsername(username);

        var account = findByEmail(username);

        if (jwtService.validateToken(refreshToken, userDetails, TokenType.REFRESH_TOKEN) && !redisService.existData(refreshToken)) {
            accessToken = jwtService.generateToken(username, TokenType.ACCESS_TOKEN);
        } else throw new KoiException(ResponseCode.JWT_INVALID);

        var memberId = memberService.getMemberIdByAccount(account);



        AuthenticateResponse tokenResponse = AuthenticateResponse.builder()
                .account(accountEntityToDtoConverter.convertAccount(account))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();


        return tokenResponse;
    }

    @Override
    public void createAccountStaff(AccountRegisterDto staffDto) {
        Account account = new Account();
        if(accountRepository.existsByEmail(account.getEmail()))
            throw new KoiException(ResponseCode.EMAIL_ALREADY_EXISTS);
        modelMapper.map(staffDto, account);
        account.setPassword(passwordEncoder.encode(staffDto.getPassword()));
        account.setRole(AccountRoleEnum.STAFF);
        account.setStatus(true);
        accountRepository.save(account);
    }

    @Override
    public List<Account> getAllStaff() {
        List<Account> list = accountRepository.findAll();
        List<Account> staffList = list
                .stream()
                .filter(staff -> staff.getRole() == AccountRoleEnum.STAFF)
                .collect(Collectors.toList());
        return staffList;
    }

    @Override
    public boolean existById(Integer accountId) {
        return accountRepository.existsById(accountId);
    }

    @Override
    public void logout(LogoutDto logoutDTO) {
        redisService.saveData(logoutDTO.getAccessToken(),"invalid",(long) 1000*60*60*24);
        redisService.saveData(logoutDTO.getRefreshToken(), "invalid",(long) 1000*60*60*24*15);
    }

    @Override
    public String forgotPassword(ForgotPasswordDto request) {
        // Check existed email
        Account account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new KoiException(ResponseCode.EMAIL_NOT_FOUND));

        // User is active or inactivated
        if(!account.isStatus())
            throw new KoiException(ResponseCode.ACCOUNT_INACTIVATED);

        // Generate reset token
        String reset_token = jwtService.generateResetToken(request.getEmail(), TokenType.RESET_TOKEN);

        // Send email confirm link

        String confirmLink = "https://prestigekoiauction.netlify.app/reset-password?reset_token=" + reset_token;

        emailService.sendEmail(request.getEmail(), "Reset Password", emailContent.createEmailResetPassword(account.getFirstName(), confirmLink));

        return "Send successfully";
    }

    @Override
    public String resetPassword(String reset_token) {

        isValidAccountByToken(reset_token);

        return "Reset successfully";
    }

    @Override
    public String changePassword(ResetPasswordDto request, String reset_token) {

        UserDetails userDetails = isValidAccountByToken(reset_token);
        Account account = findByEmail(userDetails.getUsername());

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new KoiException(ResponseCode.PASSWORD_NOT_MATCH);
        }

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(account);

        return "Change successfully";
    }

    private UserDetails isValidAccountByToken(String reset_token){
        String userName = jwtService.extractUsername(reset_token, TokenType.RESET_TOKEN);
        System.out.println(userName);
        var userDetails = accountDetailService.loadUserByUsername(userName);

        if (!jwtService.validateToken(reset_token, userDetails, TokenType.RESET_TOKEN) ) {
            throw new KoiException(ResponseCode.JWT_INVALID);
        }
        return userDetails;
    }

    @Override
    public void updatePassword(UpdatePasswordDto request) {
        Account account = getUserInfoByUsingAuth.getAccountFromAuth();

        if(!oldPasswordIsValid(account, request.getOldPassword())){
            throw new KoiException(ResponseCode.INVALID_OLD_PASSWORD);
        }
        if(!request.getNewPassword().equals(request.getConfirmNewPassword())){
            throw new KoiException(ResponseCode.PASSWORD_NOT_MATCH);
        }
        if(request.getOldPassword().equals(request.getNewPassword())){
            throw new KoiException(ResponseCode.UPDATE_SAME_PASSWORD);
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        emailService.sendEmail(account.getEmail(), "Password changed", emailContent.createEmailChangedPassword(account.getFirstName()));
    }

    private boolean oldPasswordIsValid(Account account, String oldPassword){
        return passwordEncoder.matches(oldPassword, account.getPassword());
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public void updateProfile(UpdateProfileDto request) {
        Account account = getUserInfoByUsingAuth.getAccountFromAuth();
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setPhoneNumber(request.getPhoneNumber());
        saveAccount(account);
    }

    @Override
    public List<Account> getAllAccount() {
        return accountRepository.findAll();
    }

    @Override
    public void disableAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new KoiException(ResponseCode.ACCOUNT_NOT_FOUND));

        if(account.getRole().equals(AccountRoleEnum.MANAGER)){
            throw new KoiException(ResponseCode.DISABLE_MANAGER_FAILED);
        }

        if(account.getRole().equals(AccountRoleEnum.BREEDER)) {
            account.getKoiBreeder().setStatus(false);
        }

        account.setStatus(false);
        saveAccount(account);
    }

    @Override
    public Account createManagerAccount() {
        Account manager = Account.builder()
                .email("manager@gmail.com")
                .password(passwordEncoder.encode("@manager1"))
                .firstName("Manager")
                .lastName("KoiAuction")
                .phoneNumber("0928173628")
                .role(AccountRoleEnum.MANAGER)
                .status(true)
                .build();
        if(!accountRepository.findByEmail("manager@gmail.com").isEmpty()){
            return null;
        }
        return accountRepository.save(manager);
    }
}