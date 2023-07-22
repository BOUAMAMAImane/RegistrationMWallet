package stg.payit.wallet.security.filters;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    // Jackson JSON serializer instance
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException exception
    ) throws IOException, ServletException { 
        HttpStatus httpStatus = HttpStatus.FORBIDDEN; // 403        

        Map<String, Object> data = new HashMap();
        data.put(
          "timestamp", 
          new java.util.Date()
        );
        data.put(
          "code", 
          httpStatus.value()
        );
        data.put(
          "status", 
          httpStatus.name()
        );
        data.put(
          "message", 
          exception.getMessage()
        );
        
        // setting the response HTTP status code
        response.setStatus(httpStatus.value());
        
        // serializing the response body in JSON
        response
          .getOutputStream()
          .println(
              objectMapper.writeValueAsString(data)
          );
    }
}