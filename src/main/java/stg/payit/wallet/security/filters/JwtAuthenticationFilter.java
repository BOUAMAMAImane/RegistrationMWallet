package stg.payit.wallet.security.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NoArgsConstructor;
import org.apache.catalina.connector.Response;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Dynamic;

import lombok.AllArgsConstructor;
import stg.payit.wallet.appuser.AppUser;
import stg.payit.wallet.appuser.AppUserRepository;
import stg.payit.wallet.appuser.AppUserService;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final String CIPHER_ALGORITHM = "AES/CBC/ISO10126PADDING";
	static byte[] iv = hexStringToByteArray("48E53E0639A76C5A5E0C5BC9E3A91538");

	private final AppUserService appUserService;
	private final AuthenticationManager authenticationManager;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AppUserService appUserService) {
		this.authenticationManager = authenticationManager;
		this.appUserService = appUserService;
	}


	/*private AuthenticationManager authenticationManager;
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}*/
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		String phone_number = request.getParameter("phoneNumber");
		String password = request.getParameter("password");
		String currentDeviceId = request.getParameter("deviceId");
		System.out.println("Current Device ID: " + currentDeviceId);

		// Récupérer le deviceId de l'utilisateur en utilisant le numéro de téléphone

		Optional<String> storedDeviceIdOptional = appUserService.findDeviceIdByPhoneNumber(phone_number);
		String storedDeviceId = storedDeviceIdOptional.orElse("valeur_par_defaut");

		// Comparer les deviceId
		if (currentDeviceId.equals(storedDeviceId)) {
			// Les deviceId correspondent, afficher un message
			System.out.println("Le deviceId correspond. Utilisateur authentifié.");
		} else {
			// Les deviceId ne correspondent pas, afficher un autre message
			System.out.println("Le deviceId ne correspond pas. Veuillez vous authentifier.");
		}
		String session = request.getSession().getAttribute("uuid").toString();
		byte[] decodedKey = new Base64(true).decode(session);
		Key secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		byte[] s = new Base64().encode(decodedKey);
		String ss = new String(s);
		if(ss.contains("/"))
		{
			ss = ss.replaceAll("/", "B");
		}
		String pwd = decrypt(password,ss);
		System.out.println(pwd);
		UsernamePasswordAuthenticationToken authenticationToken=null;

		try {
			authenticationToken =
					new UsernamePasswordAuthenticationToken(phone_number,pwd);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return authenticationManager.authenticate(authenticationToken);
	}
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
											Authentication authResult) throws IOException, ServletException {

		AppUser appUser = (AppUser) authResult.getPrincipal();
		Algorithm algorithm = Algorithm.HMAC384("hps-secret-123*$");
		String jwtAccess = JWT.create()
				.withSubject(appUser.getFirstName())
				.withExpiresAt(new Date(System.currentTimeMillis()+5*60*1000))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", appUser.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()))
				.sign(algorithm);

		Map<String,Object> resp = new HashMap<>();
		response.setContentType("application/json");
		response.setHeader("authorization", jwtAccess);
		resp.put("data", appUser);
		resp.put("token" , jwtAccess);
		resp.put("status",200);
		resp.put("message", "Login Successful");
		new ObjectMapper().writeValue(response.getOutputStream(), resp);

	}
	public static String decrypt(String data) throws ParseException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {


		String sessionid = RequestContextHolder.currentRequestAttributes().getSessionId();
		sessionid = sessionid.replaceAll("_","A");
		sessionid = sessionid.replaceAll("-","B");
		sessionid = sessionid.substring(0, 32);


		byte[] encryptedData = new Base64().decode(data);
		Cipher c = null;
		// Cipher decryptCipher = Cipher.getInstance(CIPHER_ALGORITHM);
		try {
			c = Cipher.getInstance(CIPHER_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Key k = parseSecretKey(sessionid);
		try {
			c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] decrypted = null;
		try {
			decrypted = c.doFinal(encryptedData);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(decrypted);
	}


	//********************************************************************************************************//
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



	public static String decrypt( String strToDecrypt,  String sessionid) {
		try {

			Key secretKey = parseSecretKey(sessionid);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE,secretKey, new IvParameterSpec(iv));

			byte[] encryptedData = new Base64().decode(strToDecrypt);
			return new String(cipher.doFinal(encryptedData));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return "ERROR";
	}


}