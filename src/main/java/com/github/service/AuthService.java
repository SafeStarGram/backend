package com.github.service;

import com.github.dto.LoginRequest;
import com.github.dto.SignUpDto;
import com.github.entity.UserEntity;
import com.github.repository.UserJdbcRepository;
import com.github.token.RefreshTokenStore;
import com.github.dto.TokenResponse;
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



    @Transactional
    public void join(SignUpDto signUpDto) {

        if (userJdbcRepository.existsByEmail(signUpDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        UserEntity userEntity = UserEntity.builder()
                .email(signUpDto.getEmail())
                .name(signUpDto.getName())
                .password(signUpDto.getPassword()) // <<encode필요
                .build();

        userJdbcRepository.save(userEntity);
    }


    public TokenResponse login(LoginRequest request) {
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

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(props.getAccessTtl().toSeconds())
                .build();
    }
}

