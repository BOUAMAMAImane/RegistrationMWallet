package stg.payit.wallet.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String phoneNumber;
    private final String cin;
    private final String gender;
    private final String newPassword;
    private final String deviceId; // ID du téléphone
    private final String longitude;
<<<<<<< HEAD
    private final String Latitude;
=======
    private final String Latitude;// Localisation
>>>>>>> 8a8232a9deba40f86a0615311284b941b01dbb78
    private Date loginTime;
    
}
