package io.github.nerfi58.bookster.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AccountNotActivatedException extends AuthenticationException {

    private long userId;

    public AccountNotActivatedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AccountNotActivatedException(String msg) {
        super(msg);
    }

    public AccountNotActivatedException(String msg, long userId) {
        super(msg);
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }
}
