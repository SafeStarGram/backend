package com.github.service;

import com.github.dto.ProfileResponse;
import com.github.entity.UserEntity;
import com.github.repository.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserJdbcRepository userJdbcRepository;

    public ProfileResponse getMyProfile(int userId) {

        UserEntity u = userJdbcRepository.findById(userId); //userId로 엔티티 조회
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        return ProfileResponse.builder()
                .userId(u.getUserId())
                .name(u.getName())
                .phoneNumber(u.getPhoneNumber())
                .radioNumber(u.getRadioNumber())
                .profilePhotoUrl(u.getProfilePhotoUrl())
                .departmentId(u.getDepartmentId())
                .positionId(u.getPositionId())
                .build();
    }
}
