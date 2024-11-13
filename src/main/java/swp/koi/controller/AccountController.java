package swp.koi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.koi.dto.request.*;
import swp.koi.dto.response.AuthenticateResponse;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.exception.KoiException;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.koiBreederService.KoiBreederService;

import javax.security.auth.login.AccountNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
@Tag(name = "account", description = "Everything about your account")
public class AccountController {


    private final AccountService accountService;
    private final KoiBreederService koiBreederService;

    @Operation(summary = "Login to system")
    @PostMapping("/login")
    public ResponseData<?> login(@Valid @RequestBody AccountLoginDto request) {
        try {
            var tokenResponse = accountService.login(request);

            return new ResponseData<>(ResponseCode.SUCCESS_LOGIN.getCode(),
                    "Token generated successfully",
                    tokenResponse);

        } catch (KoiException e) {
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Login by google")
    @PostMapping("/login-google")
    public ResponseData<AuthenticateResponse> loginGoogle(@RequestBody GoogleTokenRequestDto token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        AuthenticateResponse authenticateResponse = accountService.loginGoogle(token);
        return new ResponseData<>(ResponseCode.SUCCESS, authenticateResponse);
    }

    @Operation(summary = "Sign up for an account")
    @PostMapping("/signup")
    public ResponseData<String> signup(@Valid @RequestBody AccountRegisterDto request) {

        try {
            accountService.createAccountByRequest(request);
            return new ResponseData<>(ResponseCode.SUCCESS_SIGN_UP);
        } catch (KoiException e) {
            return new ResponseData<>(e.getResponseCode());
        }


    }

    @PostMapping("/refreshToken")
    public ResponseData<?> refresh(@Valid HttpServletRequest request) throws AccountNotFoundException {

        try {
            AuthenticateResponse authenticateResponse = accountService.refreshToken(request);

            return new ResponseData<>(ResponseCode.SUCCESS.getCode(),
                    "Token refreshed successfully",
                    authenticateResponse);

        } catch (KoiException e) {
            return new ResponseData<>(e.getResponseCode().getCode(),"Invalid refresh token");
        }
    }

    @Operation(summary = "Logout")
    @PostMapping("/logout")
    public ResponseData<?> logout(@Valid @RequestBody LogoutDto logoutDTO) {

        accountService.logout(logoutDTO);

        return new ResponseData<>(ResponseCode.LOGOUT_JWT);
    }

    @Operation(summary = "Send password reset link to email")
    @PostMapping("/forgot-password")
    public ResponseData<String> forgotPassword(@Valid @RequestBody ForgotPasswordDto request){

        return new ResponseData<>(ResponseCode.SUCCESS, accountService.forgotPassword(request));
    }

    @Operation(summary = "Confirm password reset link")
    @PostMapping("/reset-password")
    public ResponseData<String> resetPassword(@RequestParam String reset_token){

        return new ResponseData<>(ResponseCode.SUCCESS, accountService.resetPassword(reset_token));
    }

    @Operation(summary = "Change password")
    @PostMapping("/change-password")
    public ResponseData<String> changePassword(@Valid @RequestBody ResetPasswordDto request, @RequestParam String reset_token){

        return new ResponseData<>(ResponseCode.SUCCESS, accountService.changePassword(request, reset_token));
    }

    @Operation(summary = "Update password in profile")
    @PatchMapping("/update-password")
    public ResponseData<String> updatePassword(@Valid @RequestBody UpdatePasswordDto request){
        accountService.updatePassword(request);
        return new ResponseData<>(ResponseCode.CHANGE_PASSWORD_SUCCESS);
    }

    @Operation(summary = "Update breeder's profile")
    @PostMapping("/update-breeder-profile")
    public ResponseData<?> updateBreederProfile(@Valid @RequestBody UpdateBreederProfileDto request){
        koiBreederService.updateBreederProfile(request);
        return new ResponseData<>(ResponseCode.UPDATE_BREEDER_PROFILE_SUCCESS);
    }

    @Operation(summary = "Update account profile")
    @PostMapping("/update-profile")
    public ResponseData<?> updateProfile(@Valid @RequestBody UpdateProfileDto request){
        accountService.updateProfile(request);
        return new ResponseData<>(ResponseCode.UPDATE_PROFILE_SUCCESS);
    }
}
