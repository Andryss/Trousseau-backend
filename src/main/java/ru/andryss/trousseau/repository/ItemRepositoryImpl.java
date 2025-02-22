package ru.andryss.trousseau.repository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.ObjectMapperWrapper;
import ru.andryss.trousseau.service.TimeService;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TimeService timeService;
    private final ObjectMapperWrapper mapper;

    private final DateTimeFormatter itemIdFormatter = DateTimeFormatter.ofPattern("yyyyMM_hhmmssSSS");

    @Override
    public ItemEntity save(ItemEntity item) {
        ZonedDateTime now = timeService.now();
        String id = itemIdFormatter.format(now);
        item.setId(id);

        item.setStatus(hasRequiredFields(item) ? ItemStatus.READY : ItemStatus.DRAFT);

        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", item.getId());
        param.addValue("title", item.getTitle());
        param.addValue("mediaIds", mapper.writeValueAsString(item.getMediaIds()));
        param.addValue("description", item.getDescription());
        param.addValue("status", item.getStatus().getValue());

        jdbcTemplate.update("""
            insert into items(id, title, media_ids, description, status)
                values(:id, :title, :mediaIds::jsonb, :description, :status)
        """, param);

        return item;
    }

    private static boolean hasRequiredFields(ItemEntity item) {
        return !StringUtils.isBlank(item.getTitle())
                && !CollectionUtils.isEmpty(item.getMediaIds())
                && !StringUtils.isBlank(item.getDescription());
    }
}
