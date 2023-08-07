package stg.payit.wallet.security.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import stg.payit.wallet.appuser.AppUserService;
import stg.payit.wallet.appuser.UserConfirmation;
import stg.payit.wallet.email.EmailService;
import stg.payit.wallet.security.filters.CustomAccessDeniedHandler;
import stg.payit.wallet.security.filters.CustomAuthenticationFailureHandler;
import stg.payit.wallet.security.filters.JwtAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private EmailService emailService;
    private UserConfirmation userConfirmation;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        http.authorizeRequests().antMatchers("/login/**").permitAll();
        http.authorizeRequests().antMatchers( "/api/users/**").permitAll();


        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/registration/**")
                .permitAll()
                .anyRequest()
                .authenticated();
//        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean()));
        http.addFilter(new JwtAuthenticationFilter(authenticationManager(), appUserService,emailService,userConfirmation));

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoderr());
        provider.setUserDetailsService(appUserService);
        return provider;
    }
/*    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
   public UserConfirmation userConfirmation() {
        return new UserConfirmation();
   }*/
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Bean
    public static NoOpPasswordEncoder passwordEncoderr() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}