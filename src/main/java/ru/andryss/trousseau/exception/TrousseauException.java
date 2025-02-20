package ru.andryss.trousseau.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrousseauException extends RuntimeException {
    private final int code;
    private final String message;
    private final String humanMessage;
}
