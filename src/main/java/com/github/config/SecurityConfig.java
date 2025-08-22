package com.github.config;

import com.github.jwt.JwtTokenProvider;
import com.github.token.JwtAuthFilter;
import com.github.token.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwt;

    @Autowired(required = false)
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()); // SPA+JWT면 주로 disable (쿠키 쓰면 CSRF 설정 필요)

        http.authorizeHttpRequests(reg -> reg
                // 🔓 인증 없이 허용할 엔드포인트를 명시적으로 열어둠
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/auth/join", "/auth/login", "/auth/refresh", "/auth/logout").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .anyRequest().authenticated()
        );


        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth -> {
                if (oAuth2LoginSuccessHandler != null) {
                    oauth.successHandler(oAuth2LoginSuccessHandler);
                }
            });
        }

        http.addFilterBefore(new JwtAuthFilter(jwt), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req,res,e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"인증 필요\"}");
                })
                .accessDeniedHandler((req,res,e)->{
                    res.setStatus(403);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"code\":\"FORBIDDEN\",\"message\":\"권한 없음\"}");
                })
        );

        return http.build();
    }
}
