package swp.koi.service.accountService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import swp.koi.dto.request.*;
import swp.koi.dto.response.AuthenticateResponse;
import swp.koi.model.Account;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface AccountService {
    AccountRegisterDto findByAccountId(Integer accountId);
    Account findById(Integer accountId);
    Account createAccount(Account account);
    Account createAccountByRequest(AccountRegisterDto request);
    Account findByEmail(String emai);
    AuthenticateResponse login(AccountLoginDto request);
    AuthenticateResponse refreshToken(HttpServletRequest request) throws AccountNotFoundException;

    List<Account> getAllStaff();

    boolean existById(@NotNull(message = "Account Id is required") Integer accountId);

    void createAccountStaff(AccountRegisterDto staffDto);
    void logout(LogoutDto logoutDTO);

    AuthenticateResponse loginGoogle(GoogleTokenRequestDto googleToken);

    String forgotPassword(ForgotPasswordDto request);

    String resetPassword(String resetToken);

    String changePassword(ResetPasswordDto request, String reset_token);

    void updatePassword(UpdatePasswordDto request);

    void saveAccount(Account account);

    void updateProfile(@Valid UpdateProfileDto request);

    List<Account> getAllAccount();

    void disableAccount(Integer accountId);

}