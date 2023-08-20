package stg.payit.wallet.appuser;
import java.security.SecureRandom;
import java.util.Base64;

public class RandomSecretCodeGenerator {

    public static String generateRandomSecretCode() {
        // Créez un générateur de nombres aléatoires sécurisés
        SecureRandom secureRandom = new SecureRandom();

        // Générez un tableau de bytes aléatoires
        byte[] randomBytes = new byte[6];
        secureRandom.nextBytes(randomBytes);

        // Encodez les bytes en base64 pour obtenir une chaîne lisible
        String secretCode = Base64.getEncoder().encodeToString(randomBytes);
        System.out.println("Random Secret Code: " + secretCode);

        return secretCode;
    }
}
