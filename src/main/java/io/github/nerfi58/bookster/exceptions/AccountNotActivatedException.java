package io.github.nerfi58.bookster.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AccountNotActivatedException extends AuthenticationException {

    public AccountNotActivatedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AccountNotActivatedException(String msg) {
        super(msg);
    }
}
