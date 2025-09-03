package com.github.repository;

import com.github.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbc;

    public void insert(PostEntity e) {
        final String sql =
                "INSERT INTO post " +
                        "(sub_area_id, repoter_id, title, content, reporter_risk, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            if (e.getSubAreaId() == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, e.getSubAreaId());
            if (e.getRepoterId() == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, e.getRepoterId());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getContent());
            ps.setString(5, e.getRepoterRisk());
            ps.setTimestamp(6, Timestamp.valueOf(e.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(e.getUpdatedAt()));
            return ps;
        });
    }
}
