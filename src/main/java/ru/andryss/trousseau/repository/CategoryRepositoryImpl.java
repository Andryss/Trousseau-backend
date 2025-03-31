package ru.andryss.trousseau.repository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
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
}
