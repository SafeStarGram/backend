package com.github.repository;

import com.github.dto.DepartmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DepartmentJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<DepartmentDto> findAll() {
        String sql = "SELECT department_id, name FROM department";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new DepartmentDto(
                        rs.getInt("department_id"),
                        rs.getString("name")
                )
        );
    }

}
