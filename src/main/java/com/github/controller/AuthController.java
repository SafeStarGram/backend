package com.github.controller;


import com.github.dto.LoginRequest;
import com.github.dto.SignUpDto;
import com.github.service.AuthService;
import com.github.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody SignUpDto signUpDto) {
        authService.join(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공입니다.");
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email/password는 필수");
        }
        return authService.login(request);
    }

}
