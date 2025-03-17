package ru.andryss.trousseau.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.exception.TrousseauException;
import ru.andryss.trousseau.generated.model.ErrorObject;

@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice {
    @ExceptionHandler(TrousseauException.class)
    ErrorObject handleTrousseauException(TrousseauException e, HttpServletResponse response) {
        response.setStatus(extractStatusCode(e));
        return new ErrorObject()
                .code(e.getCode())
                .message(e.getMessage())
                .humanMessage(e.getHumanMessage());
    }
    @ExceptionHandler(Exception.class)
    ErrorObject handleException(Exception e, HttpServletResponse response) {
        log.error("Resolved unhandled exception", e);
        return handleTrousseauException(Errors.unhandledExceptionError(), response);
    }

    private static int extractStatusCode(TrousseauException e) {
        int code = e.getCode();
        if (code >= 400 && code < 500) { // 400..499
            return 400;
        }
        return 500;
    }
}
