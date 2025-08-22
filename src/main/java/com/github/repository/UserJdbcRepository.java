package com.github.repository;

import com.github.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

//    private UserEntity mapRow(ResultSet rs, int rowNum) {
//
//        return UserEntity.builder()
//                .email(rs.getString("email"))
//                .name(rs.getString("name"))
//                .password(rs.getString("password"))
//                .build();
//    }


    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                email
        );
        return count != null && count > 0;
    }

    public Long save(UserEntity userEntity) {
        String sql = "INSERT INTO users(email, name , password) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, userEntity.getEmail());
            ps.setString(2, userEntity.getName());
            ps.setString(3, userEntity.getPassword());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();


    }
}
