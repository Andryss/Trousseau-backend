package ru.andryss.trousseau.exception;

import ru.andryss.trousseau.model.ItemStatus;

public class Errors {
    public static TrousseauException mediaSaveError() {
        return new TrousseauException(503, "media.save.error", "Ошибка при сохранении медиа");
    }
    public static TrousseauException illegalItemStatusTransition(ItemStatus from, ItemStatus to) {
        return new TrousseauException(409, "item.status.change.error",
                String.format("Переход из статуса %s в статус %s запрещен", from.getValue(), to.getValue()));
    }
}
