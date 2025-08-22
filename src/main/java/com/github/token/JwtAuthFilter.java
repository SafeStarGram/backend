package com.github.token;

import com.github.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwt.validate(token)) {
                String userId = String.valueOf(jwt.getUserId(token));
                String role = jwt.getRole(token);

                var auth = new UsernamePasswordAuthenticationToken(
                        userId, null,
                        role != null ? List.of(new SimpleGrantedAuthority(role)) : List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req, res);
    }

}
