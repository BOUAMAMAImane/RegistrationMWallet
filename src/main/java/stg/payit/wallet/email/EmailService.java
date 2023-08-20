package stg.payit.wallet.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("noreply.payit@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    @Async
    public void sendAuthenticationQuestionByEmail(String email,String address) {

        String subject = "Vérification d'identité";
        String question = "Il s'agit bien de vous ?";

        // Le lien à inclure dans l'e-mail
        String lienConfirmation = "http://192.168.1.38:8040/wallet_war/registration/confirmer-identite?reponse=oui";

        String message = "Bonjour,\n\nPour des raisons de sécurité, nous avons besoin de vérifier votre identité.\n"
                + "Votre adresse actuelle est : " + address + "\n"
                + "Veuillez" +lienConfirmation + "utiliser le bouton ci-dessous pour confirmer qu'il s'agit bien de vous :\n\n" + question;

        // Configuration des propriétés pour l'envoi de l'e-mail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Votre adresse e-mail et mot de passe pour l'authentification SMTP
        final String username = "noreply.payit@gmail.com";
        final String password = "rlwxseuhkoniqlnm";

        // Créer une session pour l'envoi de l'e-mail
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Créer un objet MimeMessage pour composer l'e-mail
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            // Envoyer l'e-mail
            Transport.send(mimeMessage);
            System.out.println("E-mail envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void sendSecretCodeByEmail(String email, String secretCode) {
        String subject = "Your Secret Code";
        String message = "Hello,\n\nHere is your secret code: " + secretCode;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom("noreply@example.com"); // Remplacez par votre adresse e-mail
            mailSender.send(mimeMessage);
            System.out.println("Secret code sent successfully to: " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to send secret code by email");
        }
}
}
