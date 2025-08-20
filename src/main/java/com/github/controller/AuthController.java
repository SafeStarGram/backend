package com.github.controller;


import com.github.dto.SignUpDto;
import com.github.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody SignUpDto signUpDto) {
        authService.join(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }




}
