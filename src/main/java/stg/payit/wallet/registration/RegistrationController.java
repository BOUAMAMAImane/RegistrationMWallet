package stg.payit.wallet.registration;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.view.RedirectView;

import lombok.AllArgsConstructor;
import stg.payit.wallet.appuser.AppUser;
import stg.payit.wallet.appuser.AppUserRepository;
import stg.payit.wallet.appuser.AppUserService;
import stg.payit.wallet.appuser.UserConfirmation;
import stg.payit.wallet.registration.RegistrationService;
import stg.payit.wallet.responseHandler.ResponseHandler;
import stg.payit.wallet.security.filters.JwtAuthenticationFilter;

import static stg.payit.wallet.appuser.RandomSecretCodeGenerator.generateRandomSecretCode;


@RestController
@CrossOrigin("*")
@RequestMapping(path = "registration")
@AllArgsConstructor
public class RegistrationController {
	private static final String CIPHER_ALGORITHM = "AES/CBC/ISO10126PADDING";
	static byte[] iv = hexStringToByteArray("48E53E0639A76C5A5E0C5BC9E3A91538");
	private List<String> activeDeviceIds = new ArrayList<>();
	private final RegistrationService registrationService;
	private final AppUserRepository appUserRepository;
	private final AppUserService appUserService;
	private UserConfirmation userConfirmation = new UserConfirmation();
	@PostMapping
	public ResponseEntity<Object> register(@RequestBody RegistrationRequest request) {
		if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
			return ResponseHandler.generateResponse("Email Already Exists", HttpStatus.OK, null);
		}

		return registrationService.register(request);
	}
	@GetMapping("/confirmer-identite")
	public ResponseEntity<String> confirmerIdentite(@RequestParam("reponse") String reponse) {
		CompletableFuture<String> userResponseFuture = new CompletableFuture<>();

		userConfirmation.setUserResponseAsync(userResponseFuture);

		userResponseFuture.complete(reponse);
		System.out.println("Réponse de l'utilisateur stockée : " + userConfirmation.getUserResponse());

		if ("oui".equalsIgnoreCase(reponse)) {
			return ResponseEntity.ok("Merci pour la confirmation. Vous pouvez maintenant accéder à l'application depuis le nouvel appareil.");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé. Votre identité n'a pas été confirmée.");
		}
	}
	@GetMapping("/recuperer-reponse")
	public ResponseEntity<String> recupererReponse() {
		// Récupérer la réponse stockée dans UserConfirmation
		String reponseUtilisateur = userConfirmation.getUserResponse();
		// Vérifier la réponse pour décider du message à renvoyer
		if ("oui".equalsIgnoreCase(reponseUtilisateur)) {
			return ResponseEntity.ok("Vous avez répondu : oui");
		} else {
			return ResponseEntity.ok("Vous avez répondu : " + reponseUtilisateur);
		}
	}
	@GetMapping("sessionId")
	public String sessionIds() throws ParseException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {



		String sessionid = RequestContextHolder.currentRequestAttributes().getSessionId();


		byte[] decodedKey = new Base64(true).decode("oJ8z4yvbYB6r_dQ5EP080djUyWLBExkM");
		Key secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//		Key secretKey = parseSecretKey(sessionid);


		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

		byte[] encryptedMessage = cipher.doFinal("8a5ea65070b3af3c152847ba88772b69367e4923".getBytes());
		byte[] encryptedByteValue = new Base64().encode(encryptedMessage);

		String encryptedValue = new String(encryptedByteValue);
		System.out.println("------------------ENCRYPTE MESSAGE ---------------");
		System.out.println(encryptedValue);
		return encryptedValue + "---------"+sessionid;

	}
	@GetMapping("session")
	public String sessionId(HttpServletRequest request) throws ParseException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		//HttpSession session = request.getSession();
		//	 System.out.println(session.getAttribute("UserName"));


//		 byte[] decodedKey = new Base64(true).decode("ZUXRxkanVL7FATY-zT41XqiV0WuCrF9LFuDfVZbB".substring(0,32));
//		 Key secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//		byte[] s = new Base64().encode(decodedKey);
//		String ss = new String(s);

		HttpSession session = request.getSession();
		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		System.out.println(uuid);
		session.setAttribute("uuid",uuid);
		return uuid;
	}

	@GetMapping("sessionTest")
	public String session(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();

		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		String uuids = UUID.randomUUID().toString().replaceAll("-","");
		System.out.println(uuid);
		System.out.println(uuids);
		session.setAttribute("uuid",uuid);
		return uuid;

	}
	@GetMapping("userbygender")
	public List<AppUser> getUsersByGender(@RequestParam(name = "gender") String gender){
		return appUserService.getUsersByGender(gender);
	}


	@PutMapping("changepassword")
	public ResponseEntity<Object> changePassword(@RequestBody RegistrationRequest registrationRequest) {
		System.out.println("hello cett fct est fonctionnelle changepassword dans controller ! ");
		Optional<AppUser> user = appUserRepository.findByEmail(registrationRequest.getEmail());
		if (user.isPresent()) {
			return appUserService.changePassword(user,registrationRequest);
		}
		else
			return ResponseHandler.generateResponseString("User not found", HttpStatus.OK);
	}
	@PutMapping("makenewpassword")
	public ResponseEntity<Object> makenewPassword(@RequestBody RegistrationRequest registrationRequest) {
		System.out.println("hello cett fct est fonctionnelle changepassword dans controller ! ");
		Optional<AppUser> user = appUserRepository.findByEmail(registrationRequest.getEmail());
		if (user.isPresent()) {
			return appUserService.makenewPassword(user,registrationRequest);
		}
		else
			return ResponseHandler.generateResponseString("User not found", HttpStatus.OK);
	}
	@PostMapping("/sendMotsecret")
	public ResponseEntity<String> sendMotsecret(@RequestParam("user_email") String userEmail) {
		try {
			appUserService.sendSecretCodeByEmail(userEmail);
			return ResponseEntity.ok("Mot secret envoyé avec succès !");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors de l'envoi du mot secret.");
		}
	}
	@GetMapping("/secret/{email}")
	public ResponseEntity<String> getSecretByEmail(@PathVariable String email) {
		String secret = appUserService.getSecretByEmail(email);
		if (secret != null) {
			return ResponseEntity.ok(secret);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@GetMapping(path = "confirm")
	public RedirectView confirm(@RequestParam("token") String token) {
		registrationService.confirmToken(token);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("https://testingg.page.link/open");
		return redirectView;
	}
	@PostMapping(path = "fcm_token")
	public ResponseEntity<Object> fcm(
			@RequestParam("device_token") String fcmToken,
			@RequestParam("user_email") String email) {
		AppUser user = appUserRepository.findByEmail(email).get();
		return appUserService.addToken(fcmToken, user);

	}
	@PostMapping(path = "remove_fcm_token")
	public ResponseEntity<Object> remove_fcm(
			@RequestParam("user_email") String email) {
		AppUser user = appUserRepository.findByEmail(email).get();
		return appUserService.removeToken(user);

	}
	@GetMapping(path = "allUsers")
	public ResponseEntity<Object> allusers() {
		return appUserService.getUsers();
	}

	@GetMapping("user")
	public ResponseEntity<Object> loadUserByEmaill(@RequestParam("email") String email) {
		return appUserService.loadUserByemail(email);
	}
	@GetMapping("userByphone")
	public ResponseEntity<Object> loadUserByphone(@RequestParam("phone_number") String phone_number) {
		return appUserService.loadUserByPhoneNumber(phone_number);
	}
	@GetMapping("userByphonetransfer")
	public Optional<AppUser> loadUserByphonetransfer(@RequestParam("phone_number") String phone_number) {
		return appUserService.loadUserByPhoneNumbertransfer(phone_number);
	}

	@GetMapping("verifypn")
	public Boolean loadUserByphonee(@RequestParam("phone_number") String phone_number) {
		return appUserService.loadUserByPhoneNumberr(phone_number);
	}
	@GetMapping("verifycin")
	public Boolean existCin(@RequestParam("cin") String cin) {
		Optional<AppUser> user = appUserRepository.findByCin(cin);
		if (user.isPresent()) {
			return true;
		}

		return false;
	}

	@GetMapping("verifyEmail")
	public Boolean existEmail(@RequestParam("email") String email) {
		Optional<AppUser> user = appUserRepository.findByEmail(email);
		if (user.isPresent()) {
			return true;
		}

		return false;
	}

	@GetMapping("test")
	public String test() {
		return "working";
	}

	@PutMapping("retierSolde")
	public String retierSolde(@RequestParam("phone_number") String phone_number,@RequestParam("montant") double montant){
		return appUserService.retierSolde(phone_number,montant);
	}

	public static SecretKey parseSecretKey(String secretKey) throws ParseException {
		return new SecretKeySpec(stringToByteArray(secretKey), "AES");
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
	public static byte[] stringToByteArray(String hexaString) throws ParseException {
		// the padding shouldn't be used, so a random one was chosen
		return stringToByteArray(hexaString, hexaString.length() / 2, (byte) 0xFF);
	}

	public static byte[] stringToByteArray(String hexaString, int resultArraySize, byte padding) throws ParseException {
		final int HEXA_RADIX = 36;
		int length = hexaString.length();
		if (length % 2 == 0) {
			length /= 2;
			if (length <= resultArraySize) {
				byte[] numbers = new byte[resultArraySize];
				int i;
				// filling the array
				for (i = 0; i < length; i++) {
					// the following line will trigger a NumberFormatException if str contains a non
					// 0-9 A-F character
					try {
						int j = Integer.parseInt(hexaString.substring(2 * i, 2 * i + 2), HEXA_RADIX);
						numbers[i] = (byte) (j & 0xFF);
					} catch (NumberFormatException ex) {
						throw new ParseException(ex.getMessage(), i);
					}
				}
				// padding
				for (; i < resultArraySize; i++) {
					numbers[i] = padding;
				}
				return numbers;
			} else {
				throw new ParseException(
						"the resulting array size is too big compared to the min size specified in the parameters", 0);
			}
		} else {
			throw new ParseException("string length must be even", 0);
		}
	}








}