package stg.payit.wallet.appuser;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class UserConfirmation {
    private String userResponse;
    private boolean isConfirmationReceived;

    public synchronized boolean isConfirmationReceived() {
        return isConfirmationReceived;
    }

    public synchronized void setUserResponse(String userResponse) {
        this.userResponse = userResponse;
        this.isConfirmationReceived = true;
    }

    public String getUserResponse() {
        return userResponse;
    }

    public void resetUserResponse() {
        userResponseFuture = new CompletableFuture<>();
    }

    private CompletableFuture<String> userResponseFuture = new CompletableFuture<>();

    public CompletableFuture<String> getUserResponseAsync() {
        return userResponseFuture;
    }

    public void setUserResponseAsync(CompletableFuture<String> responseFuture) {
        userResponseFuture = responseFuture;
    }
}
