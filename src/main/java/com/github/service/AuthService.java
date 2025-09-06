package com.github.service;

import com.github.dto.LoginRequest;
import com.github.dto.SignUpDto;
import com.github.entity.UserEntity;
import com.github.repository.UserJdbcRepository;
import com.github.token.RefreshTokenStore;
import com.github.dto.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.jwt.JwtProperties;
import com.github.jwt.JwtTokenProvider;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserJdbcRepository userJdbcRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt
    private final JwtTokenProvider jwt;
    private final JwtProperties props;
    private final RefreshTokenStore refreshStore;


    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(false); // 프론트엔드에서 접근 가능
        cookie.setSecure(false); // HTTPS에서만 전송 (개발환경에서는 false)
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        cookie.setMaxAge(60 * 60 * 24 * 13); // 13일 (refresh token 만료시간과 동일)
        cookie.setDomain(null); // 모든 도메인에서 사용 가능
        response.addCookie(cookie);
    }


    public TokenResponse loginWithoutCookie(LoginRequest request) {
        UserEntity user = userJdbcRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        long uid = user.getUserId().longValue();
        String role = "ROLE_USER";

        String access = jwt.generateAccessToken(uid, role);
        String jti = jwt.newJti();
        String refresh = jwt.generateRefreshToken(uid, role, jti);
        refreshStore.save(uid, jti);

        // 쿠키 설정 없이 응답 본문에만 포함
        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(props.getAccessTtl().toSeconds())
                .userId(user.getUserId().longValue())
                .build();
    }

    @Transactional
    public void join(SignUpDto signUpDto) {

        if (userJdbcRepository.existsByEmail(signUpDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        UserEntity userEntity = UserEntity.builder()
                .email(signUpDto.getEmail())
                .name(signUpDto.getName())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .build();

        userJdbcRepository.save(userEntity);
    }


    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        UserEntity user = userJdbcRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        long uid = user.getUserId().longValue();
        String role = "ROLE_USER";

        String access = jwt.generateAccessToken(uid, role);
        String jti = jwt.newJti();
        String refresh = jwt.generateRefreshToken(uid, role, jti);
        refreshStore.save(uid, jti);

        // RefreshToken을 쿠키로 설정
        setRefreshTokenCookie(response, refresh);

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(props.getAccessTtl().toSeconds())
                .userId(user.getUserId().longValue())
                .build();
    }
}

