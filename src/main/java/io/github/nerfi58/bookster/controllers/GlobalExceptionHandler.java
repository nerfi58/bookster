package io.github.nerfi58.bookster.controllers;

import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.TokenNotValidException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @ExceptionHandler(TokenNotValidException.class)
    public void handleTokenNotValidException(HttpServletResponse response) {

        response.addHeader("HX-Redirect", "/register?tokenNotValid");
    }

    @ExceptionHandler(Exception.class)
    public void handleEveryOtherException(HttpServletResponse response, Exception e) throws Exception {

        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        response.addHeader("HX-Redirect", "/error");
    }
}
