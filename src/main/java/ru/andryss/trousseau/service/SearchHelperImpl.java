package ru.andryss.trousseau.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.generated.model.FilterInfo;
import ru.andryss.trousseau.generated.model.PageInfo;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.generated.model.SortInfo;
import ru.andryss.trousseau.generated.model.SortOrder;

@Slf4j
@Service
public class SearchHelperImpl implements SearchHelper {

    @Override
    public String formQuery(SearchInfo info) {
        Map<String, String> params = Map.of(
                "where", getFilterQuery(info),
                "orderBy", getOrderQuery(info),
                "pageCondition", getPageCondition(info),
                "limit", getLimitQuery(info)
        );

        return new StringSubstitutor(params)
                .replace("""
                        select *
                        from items
                        where (:where)
                            and (:pageCondition)
                        order by :orderBy
                        limit :limit
                        """);
    }

    private static String getFilterQuery(SearchInfo info) {
        FilterInfo filter = info.getFilter();

        List<String> conditions = new ArrayList<>();
        for (String cond : filter.getConditions()) {

            int equalSignIndex = cond.indexOf("=");
            if (equalSignIndex != -1) {
                String field = cond.substring(0, equalSignIndex);
                String value = cond.substring(equalSignIndex + 1);
                conditions.add(String.format("%s = '%s'", field, value));
                continue;
            }

            log.warn("Unknown condition {}", filter);
        }

        String searchText = info.getText();
        if (searchText != null && !searchText.isBlank()) {
            enrichConditionsWithTextSearch(conditions, searchText.trim());
        }


        if (conditions.isEmpty()) {
            return "true";
        }

        StringJoiner joiner = new StringJoiner(") and (", "(", ")");
        for (String condition : conditions) {
            joiner.add(condition);
        }
        return joiner.toString();
    }

    private static void enrichConditionsWithTextSearch(List<String> conditions, String text) {
        for (String field : List.of("title", "description")) {
            Map<String, String> params = Map.of(
                    "field", field,
                    "text", text
            );

            conditions.add(new StringSubstitutor(params)
                    .replace("""
                            :field like '%:text%'
                            """));
        }
    }

    private static String getOrderQuery(SearchInfo info) {
        SortInfo sort = info.getSort();

        Map<String, String> params = Map.of(
                "field", sort.getField().getValue(),
                "order", sort.getOrder().getValue()
        );

        return new StringSubstitutor(params)
                .replace("""
                        :field :order, id asc
                        """);
    }

    private static String getPageCondition(SearchInfo info) {
        String pageToken = info.getPage().getToken();
        if (pageToken == null) {
            return "true";
        }

        SortInfo sort = info.getSort();

        Map<String, String> params = Map.of(
                "field", sort.getField().getValue(),
                "sign", (sort.getOrder() == SortOrder.ASC ? ">" : "<"),
                "id", pageToken
        );

        return new StringSubstitutor(params)
                .replace("""
                        :field :sign (select :field from items where id = ':id')
                        """);
    }

    private static String getLimitQuery(SearchInfo info) {
        PageInfo page = info.getPage();
        return page.getSize().toString();
    }
}
