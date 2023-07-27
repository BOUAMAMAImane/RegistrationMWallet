package stg.payit.wallet.security.filters;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class NominatimReverseGeocoder {

    public static String getAddress(double latitude, double longitude) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(
                new URI("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude));

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream inputStream = entity.getContent();
                 Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
                String responseBody = scanner.hasNext() ? scanner.next() : "";
                // Traiter la réponse JSON et extraire l'adresse complète
                // La réponse JSON contient les détails de l'adresse, vous pouvez extraire les informations pertinentes ici
                // Par exemple, vous pouvez utiliser une bibliothèque JSON comme Jackson ou Gson pour traiter la réponse JSON.
                return responseBody; // Retourne la réponse JSON complète pour l'instant, à adapter selon vos besoins
            }
        } else {
            System.out.println("Erreur lors de la requête de géocodage inverse. Pas de contenu dans la réponse.");
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            double latitude = 35.144609034100924; // Remplacez par la latitude souhaitée
            double longitude = -3.85035357667466; // Remplacez par la longitude souhaitée

            String address = getAddress(latitude, longitude);
            System.out.println("Adresse : " + address);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération de l'adresse : " + e.getMessage());
        }
    }
}
