package tip.truestrength.com.appuser.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tip.truestrength.com.appuser.AppUser;
import tip.truestrength.com.appuser.AppUserRepository;
import tip.truestrength.com.appuser.AppUserRole;
import tip.truestrength.com.appuser.AppUserService;
import tip.truestrength.com.appuser.registration.token.ConfirmationToken;
import tip.truestrength.com.appuser.registration.token.ConfirmationTokenService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if(!isValidEmail){
            throw new IllegalStateException("Email no Valido");
        }

        return appUserService.singUpUser(
            new AppUser(
                    request.getName(),
                    request.getUserName(),
                    request.getEmail(),
                    request.getPassword(),
                    AppUserRole.USER
            )
        );
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
            .getToken(token)
            .orElseThrow(() ->
                new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpireAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
            confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }


}
