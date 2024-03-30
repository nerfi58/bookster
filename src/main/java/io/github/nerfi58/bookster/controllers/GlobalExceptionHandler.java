package io.github.nerfi58.bookster.controllers;

import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.TokenNotValidException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public void handleUsernameAlreadyExistsException(HttpServletResponse response) {

        response.addHeader("HX-Redirect", "/register?usernameAlreadyExists");
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public void handleEmailAlreadyExistsException(HttpServletResponse response) {

        response.addHeader("HX-Redirect", "/register?emailAlreadyExists");
    }

    @ExceptionHandler({TokenNotValidException.class, EntityNotFoundException.class})
    public void handleError(HttpServletResponse response) {
        response.addHeader("HX-Redirect", "/error");
    }
}
