package io.github.nerfi58.bookster.config;

import io.github.nerfi58.bookster.security.LoginPageFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationProvider = authenticationProvider;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(authenticationFailureHandler)

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                )
                .addFilterBefore(new LoginPageFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
