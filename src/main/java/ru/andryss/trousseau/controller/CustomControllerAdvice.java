package ru.andryss.trousseau.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andryss.trousseau.exception.TrousseauException;
import ru.andryss.trousseau.generated.model.ErrorObject;

@RestControllerAdvice
public class CustomControllerAdvice {
    @ExceptionHandler(TrousseauException.class)
    ErrorObject handleTrousseauException(TrousseauException e) {
        return new ErrorObject()
                .code(e.getCode())
                .message(e.getMessage())
                .humanMessage(e.getHumanMessage());
    }
}
