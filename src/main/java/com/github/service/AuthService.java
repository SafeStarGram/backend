package com.github.service;

import com.github.dto.SignUpDto;
import com.github.entity.UserEntity;
import com.github.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;

    public void join(SignUpDto signUpDto) {

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
            UserEntity user = UserEntity.builder()
                    .Email(signUpDto.getEmail())
                    .password(signUpDto.getPassword())
                    .name(signUpDto.getName())
                    .build();
        }
    }

