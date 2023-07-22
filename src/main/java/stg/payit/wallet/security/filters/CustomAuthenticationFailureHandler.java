package stg.payit.wallet.security.filters;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthenticationFailureHandler 
implements AuthenticationFailureHandler {
  private ObjectMapper objectMapper = new ObjectMapper();

  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception) 
    throws IOException, ServletException {

      response.setStatus(HttpStatus.OK.value());
      Map<String, Object> data = new HashMap();
      data.put(
        "timestamp", 
        Calendar.getInstance().getTime());
      data.put(
        "exception", 
        exception.getMessage());

      response.getOutputStream()
        .println(objectMapper.writeValueAsString(data));
  }
}