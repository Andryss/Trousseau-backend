package ru.andryss.trousseau.exception;

import org.apache.commons.text.StringEscapeUtils;
import ru.andryss.trousseau.model.ItemStatus;

public class Errors {
    public static TrousseauException unhandledExceptionError() {
        return new TrousseauException(500, "internal.error", "Что-то пошло не так...");
    }
    public static TrousseauException mediaSaveError() {
        return new TrousseauException(503, "media.save.error", "Ошибка при сохранении медиа");
    }
    public static TrousseauException illegalItemStatusTransition(ItemStatus from, ItemStatus to) {
        return new TrousseauException(409, "item.status.change.error",
                String.format("Переход из статуса %s в статус %s запрещен", from.getValue(), to.getValue()));
    }

    public static TrousseauException itemNotFound(String itemId) {
        return new TrousseauException(404, "item.absent.error",
                String.format("Объявление с id=\"%s\" не найдено", StringEscapeUtils.escapeJava(itemId)));
    }

    public static TrousseauException itemNotEditable(ItemStatus status) {
        return new TrousseauException(409, "item.update.error",
                String.format("Объявление в статусе %s не редактируется", status.getValue()));
    }

    public static TrousseauException maximumBookingsCountReached(int count) {
        return new TrousseauException(409, "bookings.count.error",
                String.format("Достигнуто максимальное количество бронирований (%s)", count));
    }

    public static TrousseauException bookingNotFound(String itemId) {
        return new TrousseauException(404, "booking.absent.error",
                String.format("Бронирования объявления с id=\"%s\" не найдено", itemId));
    }
}
