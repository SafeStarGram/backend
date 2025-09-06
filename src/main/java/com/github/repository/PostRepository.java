package com.github.repository;

import com.github.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbc;

    public void insert(PostEntity e) {
        final String sql =
                "INSERT INTO posts " +
                        "(sub_area_id, reporter_id, title, content, reporter_risk, created_at, updated_at, image_blob) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            if (e.getSubAreaId() == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, e.getSubAreaId());
            if (e.getReporterId() == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, e.getReporterId());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getContent());
            ps.setString(5, e.getReporterRisk());
            ps.setTimestamp(6, Timestamp.valueOf(e.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(e.getUpdatedAt()));

            if (e.getImageBlob() == null) ps.setNull(8, Types.BLOB);
            else ps.setBytes(8, e.getImageBlob());

            return ps;
        });
    }

    public List<PostEntity> findAll(int page, int size) {
        final String sql = "SELECT post_id, sub_area_id, reporter_id, title, content, reporter_risk, created_at, updated_at, image_blob " +
                "FROM posts ORDER BY created_at DESC LIMIT ? OFFSET ?";

        return jdbc.query(sql, (rs, rowNum) -> PostEntity.builder()
                .postId(rs.getLong("post_id"))
                .subAreaId(rs.getLong("sub_area_id"))
                .reporterId(rs.getLong("reporter_id"))
                .title(rs.getString("title"))
                .content(rs.getString("content"))
                        .imageBlob(rs.getBytes("image_blob"))
                .reporterRisk(rs.getString("reporter_risk"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build(),
                size, page * size
        );
    }
}
