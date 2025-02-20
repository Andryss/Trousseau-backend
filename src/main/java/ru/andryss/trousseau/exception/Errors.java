package ru.andryss.trousseau.exception;

public class Errors {
    public static TrousseauException mediaSaveError() {
        return new TrousseauException(503, "media.save.error", "Ошибка при сохранении медиа");
    }
}
