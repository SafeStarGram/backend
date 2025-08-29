package com.github.token;

import com.github.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final StringRedisTemplate redis;
    private final JwtProperties props;

    private String key(long userId, String jti) {
        return "refresh:" + userId + ":" + jti;
    }

    public void save(long userId, String jti) {
        redis.opsForValue().set(key(userId, jti), "1", props.getRefreshTtl());
    }

    public boolean exists(long userId, String jti) {
        Boolean has = redis.hasKey(key(userId, jti));
        return has != null && has;
    }

    public void delete(long userId, String jti) {
        redis.delete(key(userId, jti));
    }
}
