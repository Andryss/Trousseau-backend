package ru.andryss.trousseau.service;

public interface RuntimeMessagingService {
    void sendMessage(MessageInfo info);

    record MessageInfo(String receiver, String title, String content) { }
}
