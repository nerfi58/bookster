package io.github.nerfi58.bookster.security;

import io.github.nerfi58.bookster.exceptions.AccountNotActivatedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Setter
@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    private String redirectUrl = "/error";
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        if (exception instanceof BadCredentialsException) {
            setRedirectUrl("/login?badCredentials");
        } else if (exception instanceof AccountNotActivatedException) {
            setRedirectUrl("/login?notActivated");
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}
