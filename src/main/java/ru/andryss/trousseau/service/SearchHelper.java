package ru.andryss.trousseau.service;

import ru.andryss.trousseau.generated.model.SearchInfo;

public interface SearchHelper {
    String formQuery(SearchInfo info);
}
