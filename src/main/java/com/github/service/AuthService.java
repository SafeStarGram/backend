package com.github.service;

import com.github.dto.SignUpDto;
import com.github.entity.UserEntity;
import com.github.repository.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserJdbcRepository userJdbcRepository;



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


    }

