package ru.andryss.trousseau.service;

import ru.andryss.trousseau.generated.model.SignInRequest;
import ru.andryss.trousseau.generated.model.SignUpRequest;

/**
 * Сервис для работы с регистрацией/входом/выходом пользователей из системы
 */
public interface AuthService {
    /**
     * Зарегистрировать пользователя
     */
    String signUp(SignUpRequest request);

    /**
     * Выполнить вход пользователя
     */
    String signIn(SignInRequest request);

    /**
     * Завершить сессию пользователя
     */
    void signOut(String session);
}
