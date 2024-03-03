package io.github.nerfi58.bookster.controllers;

import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public void handleException(Exception exception, HttpServletResponse response) throws IOException {
        if (exception instanceof UsernameAlreadyExistsException) {
            response.sendRedirect("/login?usernameAlreadyExists");
        } else if (exception instanceof EmailAlreadyExistsException) {
            response.sendRedirect("/login?emailAlreadyExists");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
