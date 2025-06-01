package ru.andryss.trousseau.exception;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.andryss.trousseau.model.ItemStatus;

public class Errors {
    /**
     * Неожиданная неотловленная ошибка
     */
    public static TrousseauException unhandledExceptionError() {
        return new TrousseauException(500, "internal.error", "Что-то пошло не так...");
    }

    /**
     * Ошибка при сохранении медиа-данных
     */
    public static TrousseauException mediaSaveError() {
        return new TrousseauException(503, "media.save.error", "Ошибка при сохранении медиа");
    }

    /**
     * Запрещенный переход между статусами объявления
     */
    public static TrousseauException illegalItemStatusTransition(ItemStatus from, ItemStatus to) {
        return new TrousseauException(409, "item.status.change.error",
                String.format("Переход из статуса %s в статус %s запрещен", from.getValue(), to.getValue()));
    }

    /**
     * Объявление не найдено
     */
    public static TrousseauException itemNotFound(String itemId) {
        return new TrousseauException(404, "item.absent.error",
                String.format("Объявление с id=\"%s\" не найдено", StringEscapeUtils.escapeJava(itemId)));
    }

    /**
     * Категория не найдена
     */
    public static TrousseauException categoryNotFound(String categoryId) {
        return new TrousseauException(404, "category.absent.error",
                String.format("Категория с id=\"%s\" не найдена", StringEscapeUtils.escapeJava(categoryId)));
    }

    /**
     * Объявление невозможно отредактировать
     */
    public static TrousseauException itemNotEditable(ItemStatus status) {
        return new TrousseauException(409, "item.update.error",
                String.format("Объявление в статусе %s не редактируется", status.getValue()));
    }

    /**
     * Достигнуто максимально возможное количество бронирований
     */
    public static TrousseauException maximumBookingsCountReached(int count) {
        return new TrousseauException(409, "bookings.count.error",
                String.format("Достигнуто максимальное количество бронирований (%s)", count));
    }

    /**
     * Бронирование объявления не найдено
     */
    public static TrousseauException bookingNotFound(String itemId) {
        return new TrousseauException(404, "booking.absent.error",
                String.format("Бронирования объявления с id=\"%s\" не найдено", itemId));
    }

    /**
     * Подписка не найдена
     */
    public static TrousseauException subscriptionNotFound(String subscriptionId) {
        return new TrousseauException(404, "subscription.absent.error",
                String.format("Подписка с id=\"%s\" не найдена", subscriptionId));
    }

    /**
     * Ошибка валидации формата запроса
     */
    public static TrousseauException validationErrors(BindingResult errors) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : errors.getFieldErrors()) {
            builder.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(", ");
        }
        builder.setLength(builder.length() - 2);
        return new TrousseauException(400, "validation.errors", builder.toString());
    }

    /**
     * Запрещенное к использованию имя пользователя
     */
    public static TrousseauException usernameForbidden(String username) {
        return new TrousseauException(403, "user.username.forbidden",
                String.format("Имя пользователя \"%s\" нельзя использовать", username));
    }

    /**
     * Неверное имя пользователя или пароль
     */
    public static TrousseauException unauthorized() {
        return new TrousseauException(401, "user.unauthorized", "Неверный логин или пароль");
    }

    /**
     * Пользователь не найден
     */
    public static TrousseauException userNotFound(String userId) {
        return new TrousseauException(404, "user.absent",
                String.format("Пользователь \"%s\" не найден", userId));
    }
}
