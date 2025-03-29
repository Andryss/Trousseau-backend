package ru.andryss.trousseau.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.google.common.collect.ImmutableMap;
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
        ImmutableMap<String, String> params = ImmutableMap.<String, String>builder()
                .put("where", getFilterQuery(info))
                .put("orderBy", getOrderQuery(info))
                .put("pageCondition", getPageCondition(info))
                .put("limit", getLimitQuery(info))
                .build();

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
        // TODO
    }

    private static String getOrderQuery(SearchInfo info) {
        SortInfo sort = info.getSort();

        ImmutableMap<String, String> params = ImmutableMap.<String, String>builder()
                .put("field", sort.getField().getValue())
                .put("order", sort.getOrder().getValue())
                .build();

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

        ImmutableMap<String, String> params = ImmutableMap.<String, String>builder()
                .put("field", sort.getField().getValue())
                .put("sign", (sort.getOrder() == SortOrder.ASC ? ">" : "<"))
                .put("id", pageToken)
                .build();

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
