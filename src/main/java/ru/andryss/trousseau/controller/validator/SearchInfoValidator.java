package ru.andryss.trousseau.controller.validator;

import java.util.List;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.andryss.trousseau.generated.model.FilterInfo;
import ru.andryss.trousseau.generated.model.SearchInfo;

@Component
public class SearchInfoValidator implements Validator {

    private final Pattern textRegexp = Pattern.compile("[\\p{L} ]+");
    private final Pattern wordRegexp = Pattern.compile("\\w+");
    private final Pattern conditionRegexp = Pattern.compile(
            wordRegexp.pattern() + "=" + wordRegexp.pattern()
    );

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return SearchInfo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        SearchInfo info = (SearchInfo) target;

        String text = info.getText();
        if (text != null && !textRegexp.matcher(text).matches()) {
            errors.rejectValue("text", "", regexpErr(textRegexp));
        }

        String sortField = info.getSort().getField();
        if (!wordRegexp.matcher(sortField).matches()) {
            errors.rejectValue("sort.field", "", regexpErr(wordRegexp));
        }

        String pageToken = info.getPage().getToken();
        if (pageToken != null && !wordRegexp.matcher(pageToken).matches()) {
            errors.rejectValue("page.token", "", regexpErr(wordRegexp));
        }

        FilterInfo filter = info.getFilter();
        if (filter != null) {
            List<String> conditions = filter.getConditions();
            for (int i = 0; i < conditions.size(); i++) {
                String cond = conditions.get(i);
                if (!conditionRegexp.matcher(cond).matches()) {
                    errors.rejectValue("filter.conditions[" + i + "]", "", regexpErr(conditionRegexp));
                }
            }
        }
    }

    private static String regexpErr(Pattern regex) {
        return String.format("must match regex '%s'", regex.pattern());
    }
}
