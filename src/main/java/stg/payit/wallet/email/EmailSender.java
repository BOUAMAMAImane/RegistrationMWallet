package stg.payit.wallet.email;

public interface EmailSender {
    void send(String to, String email);
    void sendAuthenticationQuestionByEmail(String email,String address);
    void sendSecretCodeByEmail(String email, String secretCode);
}
