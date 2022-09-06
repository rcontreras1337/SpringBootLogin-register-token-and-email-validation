package tip.truestrength.com.appuser;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tip.truestrength.com.appuser.registration.token.ConfirmationToken;
import tip.truestrength.com.appuser.registration.token.ConfirmationTokenService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 */
@Service
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "Usuario con email %s no encontrado";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    public AppUserService(AppUserRepository appUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
        ConfirmationTokenService confirmationTokenService) {
        this.appUserRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
            .orElseThrow( () -> new UsernameNotFoundException(
                String.format(USER_NOT_FOUND_MSG, email)
            ));
    }

    public String singUpUser(AppUser appUser){
        boolean userExist = appUserRepository.findByEmail(appUser.getEmail())
            .isPresent();
        if(userExist){
            throw new IllegalStateException("El email ya existe");
        }
        String encodePassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodePassword);

        String token = UUID.randomUUID().toString();
        appUserRepository.save(appUser);
        ConfirmationToken confirmationToken = new ConfirmationToken(
            token,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(15),
            appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO: enviar correo confirmación
        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
