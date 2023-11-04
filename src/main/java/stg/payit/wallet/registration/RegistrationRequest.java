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
    private final String secret;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String phoneNumber;
    private final String cin;
    private final String gender;
    private final String newPassword;
    private final String deviceId;
    private final String longitude;
    private final String Latitude;
    private Date loginTime;
    private final String question1;
    private final String question2;
    private final String question3;


}
