package com.github.token;

import com.github.dto.TokenResponse;
import com.github.jwt.JwtProperties;
import com.github.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler  implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwt;
    private final JwtProperties props;
    private final com.github.token.RefreshTokenStore store;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();

        // 공급자별로 프로필 키가 다를 수 있으니 실제 응답에 맞춰 매핑
        String providerUserId = String.valueOf(oAuth2User.getAttributes().get("id"));     // kakao: id, google: sub
        String email          = String.valueOf(oAuth2User.getAttributes().get("email"));  // google은 기본 제공, kakao는 권한 필요

        long userId = 1L; // 예시 고정값. 실제로는 userRepo 로 조회/삽입.

        String role = "ROLE_USER";
        String access = jwt.generateAccessToken(userId, role);
        String jti = jwt.newJti();
        String refresh = jwt.generateRefreshToken(userId, role, jti);
        store.save(userId, jti);

        TokenResponse body = TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(props.getAccessTtl().toSeconds())
                .build();

        res.setStatus(200);
        res.setContentType("application/json;charset=UTF-8");
        om.writeValue(res.getWriter(), body);
    }
}
