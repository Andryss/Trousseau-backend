package ru.andryss.trousseau.service;

/**
 * Сервис для работы с моментальными сообщениями
 */
public interface RuntimeMessagingService {
    /**
     * Отправить сообщение с заданным содержимым
     */
    void sendMessage(MessageInfo info);

    record MessageInfo(String receiver, String title, String content) { }
}
