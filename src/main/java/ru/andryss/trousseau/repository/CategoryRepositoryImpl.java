package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.CategoryEntity;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<CategoryEntity> rowMapper = (rs, rowNum) -> {
        CategoryEntity category = new CategoryEntity();
        category.setId(rs.getString("id"));
        category.setParent(rs.getString("parent"));
        category.setName(rs.getString("name"));
        return category;
    };

    @Override
    public List<CategoryEntity> findAll() {
        return jdbcTemplate.query("""
                select * from categories
        """, rowMapper);
    }

    @Override
    public Optional<CategoryEntity> findById(String id) {
        List<CategoryEntity> result = jdbcTemplate.query("""
                        select * from categories where id = :id
                """, new MapSqlParameterSource()
                .addValue("id", id), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    @Override
    public List<String> findPathToRoot(String id) {
        return jdbcTemplate.queryForList("""
                with recursive flat_categories as (
                    select c.id, c.parent
                        from categories c
                            where c.id = :id
                    union
                    select c.id, c.parent
                        from categories c
                            join flat_categories fc on c.id = fc.parent
                )
                select fc.id from flat_categories fc
        """, new MapSqlParameterSource()
                .addValue("id", id), String.class);
    }
}
