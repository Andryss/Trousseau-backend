package ru.andryss.trousseau.service;

import ru.andryss.trousseau.generated.model.SearchInfo;

/**
 * Сервис для формирования запроса в БД по объекту поиска {@link SearchInfo}
 */
public interface SearchHelper {
    /**
     * Сформировать запрос к БД
     */
    String formQuery(SearchInfo info);
}
