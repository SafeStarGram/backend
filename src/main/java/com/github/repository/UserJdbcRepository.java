package com.github.repository;

import com.github.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import java.sql.PreparedStatement;
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

    public UserEntity findByEmail(String email) {
        String sql = "SELECT users_id, email, name, password FROM users WHERE email = ?";
        var list = jdbcTemplate.query(sql, (rs, rn) -> {
            UserEntity u = new UserEntity();
            u.setUserId(rs.getInt("users_id"));   // PK 컬럼명에 맞게
            u.setEmail(rs.getString("email"));
            u.setName(rs.getString("name"));
            u.setPassword(rs.getString("password"));
            return u;
        }, email);

        return list.isEmpty() ? null : list.get(0);
    }

    public UserEntity findById(Integer userId) {
        String sql = "SELECT users_id, name, phone_number, radio_number, profile_photo_url, department_id, position_id " +
                "FROM users WHERE users_id = ?";

        var list = jdbcTemplate.query(sql, (rs, rn) -> {
            UserEntity u = new UserEntity();
            u.setUserId(rs.getInt("users_id"));
            u.setName(rs.getString("name"));
            u.setPhoneNumber(rs.getString("phone_number"));
            u.setRadioNumber(rs.getString("radio_number"));
            u.setProfilePhotoUrl(rs.getString("profile_photo_url"));
            u.setDepartmentId(rs.getInt("department_id"));
            u.setPositionId(rs.getInt("position_id"));
            return u;
        }, userId);

        return list.isEmpty() ? null : list.get(0);
    }

    public void updateProfile(int userId,
                              String phoneNumber,
                              String radioNumber,
                              Integer departmentId,
                              Integer positionId,
                              String profilePhotoUrl) {
        String sql = "UPDATE users " +
                "SET phone_number = ?, " +
                "    radio_number = ?, " +
                "    department_id = ?, " +
                "    position_id = ?, " +
                "    profile_photo_url = ?, " +
                "    updated_at = NOW() " +
                "WHERE users_id = ?";

        jdbcTemplate.update(sql,
                phoneNumber,
                radioNumber,
                departmentId,
                positionId,
                profilePhotoUrl,
                userId
        );
    }

//    사진업로드
    public int updateProfilePhoto(int userId, String photoUrl) {
        String sql = "UPDATE users SET profile_photo_url = ?, updated_at = NOW() WHERE users_id = ?";
        return jdbcTemplate.update(sql, photoUrl, userId);
    }

}
