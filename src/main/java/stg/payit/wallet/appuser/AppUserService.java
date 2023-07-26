package stg.payit.wallet.appuser;

import lombok.AllArgsConstructor;
import stg.payit.wallet.email.EmailSender;
import stg.payit.wallet.registration.EmailValidator;
import stg.payit.wallet.registration.RegistrationRequest;
import stg.payit.wallet.registration.token.ConfirmationToken;
import stg.payit.wallet.registration.token.ConfirmationTokenService;
import stg.payit.wallet.responseHandler.ResponseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailValidator emailValidator;
	private final EmailSender emailSender;
    @Override
    public UserDetails loadUserByUsername(String phone_number)
            throws UsernameNotFoundException {
        return appUserRepository.findByPhoneNumber(phone_number)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, phone_number)));
    }
	public Optional<String> findDeviceIdByPhoneNumber(String phoneNumber) {
		return appUserRepository.findDeviceIdByPhoneNumber(phoneNumber);
	}


	public ResponseEntity<Object> loadUserByemail(String email)
            throws UsernameNotFoundException {
        return ResponseHandler.generateResponse("user found ", HttpStatus.OK,
        		appUserRepository.findByEmail(email)); 
               
    }
	public List<AppUser> getUsersByGender(String gender) {
		return appUserRepository.findBygenderHomme(gender);
		
	}
    public ResponseEntity<Object> loadUserByPhoneNumber(String phone_number)

            {
        		Optional<AppUser> usr = appUserRepository.findByPhoneNumber(phone_number);
        		if(usr.isPresent())
        		{	return ResponseHandler.generateResponse("user found", HttpStatus.OK, usr);}
        		else 
        			return ResponseHandler.generateResponse("user not found", HttpStatus.OK, usr);
    }
    public Optional<AppUser> loadUserByPhoneNumbertransfer(String phone_number)

    {
 
		Optional<AppUser> usr = appUserRepository.findByPhoneNumber(phone_number);
		if(usr.isPresent())
		{	return usr;}
		else 
			return usr;
    }
    public boolean loadUserByPhoneNumberr(String phone_number)
    {
 
		Optional<AppUser> usr = appUserRepository.findByPhoneNumber(phone_number);
		if(usr.isPresent())
		{	return true;
		}
		else 
			return false;
    }
    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if (userExists) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.

         
            return "User already Exist";
        }
//
//        String encodedPassword = bCryptPasswordEncoder
//                .encode(appUser.getPassword());
        MessageDigest mDigest=null;
		try {
			mDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] result = mDigest.digest(appUser.getPassword().getBytes());
        StringBuffer encodedPassword = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
        	encodedPassword.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        appUser.setPassword(encodedPassword.toString());

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(
                confirmationToken);

//        TODO: SEND EMAIL

        return token;
    }
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
    
    public ResponseEntity<Object> addToken(String fcmToken,AppUser user) {
		user.setFcm_token(fcmToken);
		appUserRepository.save(user);
		return ResponseHandler.generateResponseString("Token Saved", HttpStatus.OK);
	}
    public ResponseEntity<Object> removeToken(AppUser user) {
    	user.setFcm_token(null);
    	appUserRepository.save(user);
    	return ResponseHandler.generateResponseString("Token Removed", HttpStatus.OK);
	}
    
    public ResponseEntity<Object> getUsers() {
    	
			List<AppUser>users= appUserRepository.finAllUsers();
			 return ResponseHandler.generateResponse("all users", HttpStatus.OK, users)	;
    }

	public String retierSolde(String phone_number, double montant) {
	Optional<AppUser> appUserr=	appUserRepository.findByPhoneNumber(phone_number);
	if(appUserr.isPresent()) {
		AppUser appUser = appUserr.get();
		appUser.setSolde(montant);
		appUserRepository.save(appUser);
		return "Transaction Done";
	}
		 return "Transaction Failed";
	}

	public ResponseEntity<Object> changePassword(Optional<AppUser> user, RegistrationRequest request) {
		System.out.println("CHANGE PASSWORD");
	
		System.out.println(user.get().getPassword());
		if (request.getPassword().equals(user.get().getPassword()) ) {
			user.get().setPassword(request.getNewPassword());
			appUserRepository.save(user.get());
			return ResponseHandler.generateResponseString("password has been changed", HttpStatus.OK);
		}else

		return ResponseHandler.generateResponse("password incorrect", HttpStatus.OK, null);
	}
}
