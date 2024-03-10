package io.github.nerfi58.bookster.controllers;

import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public void handleUsernameAlreadyExistsException(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login?usernameAlreadyExists");
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public void handleEmailAlreadyExistsException(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login?emailAlreadyExists");
    }
}
