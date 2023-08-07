package stg.payit.wallet.appuser;

import lombok.AllArgsConstructor;
import stg.payit.wallet.device.Device;
import stg.payit.wallet.email.EmailSender;
import stg.payit.wallet.registration.EmailValidator;
import stg.payit.wallet.registration.RegistrationRequest;
import stg.payit.wallet.registration.token.ConfirmationToken;
import stg.payit.wallet.registration.token.ConfirmationTokenService;
import stg.payit.wallet.responseHandler.ResponseHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

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
<<<<<<< HEAD
	public void addDeviceByPhoneNumber(String phoneNumber, String newDeviceId) {
		Optional<AppUser> optionalUser = appUserRepository.findByPhoneNumber(phoneNumber);
		if (optionalUser.isPresent()) {
			AppUser user = optionalUser.get();
			Device newDevice = new Device();
			newDevice.setDeviceId(newDeviceId);
			user.addDevice(newDevice);
			appUserRepository.save(user);
		} else {
			throw new UserNotFoundException("User not found with phone number: " + phoneNumber);
		}
	}
	public class UserNotFoundException extends RuntimeException {
		public UserNotFoundException(String message) {
			super(message);
		}
	}

=======
	public Optional<String> findDeviceIdByPhoneNumber(String phoneNumber) {
		return appUserRepository.findDeviceIdByPhoneNumber(phoneNumber);
	}


>>>>>>> 8a8232a9deba40f86a0615311284b941b01dbb78
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
<<<<<<< HEAD

=======
>>>>>>> 8a8232a9deba40f86a0615311284b941b01dbb78
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
/*	public Optional<String> findDeviceIdByPhoneNumber(String phoneNumber) {
		return appUserRepository.findDeviceIdByPhoneNumber(phoneNumber);

	}*/
	/*public void addDeviceId(String phoneNumber, String newDeviceId) {
		// Récupérer l'utilisateur par son numéro de téléphone
		Optional<AppUser> userOptional = appUserRepository.findByPhoneNumber(phoneNumber);

		if (userOptional.isPresent()) {
			// Ajouter le nouveau deviceId à la liste existante
			AppUser user = userOptional.get();
			List<String> deviceIds = user.getDeviceIds();
			deviceIds.add(newDeviceId);
			user.setDeviceIds(deviceIds);

			// Sauvegarder les modifications dans la base de données
			appUserRepository.save(user);}*/
/*
			// Retourner une réponse avec le nouveau deviceId ajouté
			return ResponseHandler.generateResponseString("DeviceId ajouté avec succès", HttpStatus.OK);
		} else {
			// Si l'utilisateur n'existe pas, retourner une réponse d'erreur
			return ResponseHandler.generateResponseString("Utilisateur introuvable", HttpStatus.NOT_FOUND);
		}*/



/*	public Optional<List<String>> findDeviceIdByPhoneNumber(String phoneNumber) {
		return appUserRepository.findDeviceIdByPhoneNumber(phoneNumber);
	}*/
	public List<String> findDeviceIdByPhoneNumber(String phoneNumber) {
		return appUserRepository.findDeviceIdByPhoneNumber(phoneNumber);
	}
	public void addDeviceToUser(String phoneNumber, Device device) {
		// Chercher l'utilisateur par son numéro de téléphone
		Optional<AppUser> userOptional = appUserRepository.findByPhoneNumber(phoneNumber);
		if (userOptional.isPresent()) {
			AppUser user = userOptional.get();
			List<Device> devices = user.getDevices();
			devices.add(device);
			user.setDevices(devices);
			appUserRepository.save(user);
		} else {
			throw new EntityNotFoundException("User not found with phone number: " + phoneNumber);
		}
	}
	public Optional<String> getEmailByPhoneNumber(String phoneNumber) {
		return appUserRepository.findEmailByPhoneNumber(phoneNumber);
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
