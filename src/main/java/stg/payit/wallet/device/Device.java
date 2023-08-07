package stg.payit.wallet.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.payit.wallet.appuser.AppUser;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor@AllArgsConstructor
public class Device implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false)
    private String deviceId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private AppUser appUser;
}
