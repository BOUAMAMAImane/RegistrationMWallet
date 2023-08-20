package stg.payit.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;

@SpringBootApplication
public class WalletApplication {


	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}

}
