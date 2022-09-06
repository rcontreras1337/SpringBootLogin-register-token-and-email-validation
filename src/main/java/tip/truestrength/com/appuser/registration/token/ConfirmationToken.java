package tip.truestrength.com.appuser.registration.token;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tip.truestrength.com.appuser.AppUser;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(schema = "true_strength")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expireAt;
    private LocalDateTime confirmedAt;

    // un usuario puede tener muchos tokens de confirmación
    @ManyToOne
    @JoinColumn(
        nullable = false,
        name = "app_user_id"
    )
    private AppUser appUser;

    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expireAt,
        AppUser appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.appUser = appUser;
    }

}
