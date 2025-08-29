package com.github.repository;


import com.github.dto.PositionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PositionJdbcRepository {
    private final JdbcTemplate jdbcTemplate;


    public List<PositionDto> findAll() {
        String sql = "SELECT position_id, name FROM position";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new PositionDto(
                        rs.getInt("position_id"),
                        rs.getString("name")
                )
        );
    }
}
