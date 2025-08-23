package com.github.service;

import com.github.dto.ProfileResponse;
import com.github.dto.ProfileUpdateRequest;
import com.github.entity.UserEntity;
import com.github.repository.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // 추가: 내 프로필 수정
    @Transactional
    public ProfileResponse updateMyProfile(int userId, ProfileUpdateRequest req) {
        // 1) 사용자 존재 확인
        UserEntity exists = userJdbcRepository.findById(userId);
        if (exists == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        // 2) DB 업데이트
        userJdbcRepository.updateProfile(
                userId,
                req.getPhoneNumber(),
                req.getRadioNumber(),
                req.getDepartmentId(),
                req.getPositionId(),
                req.getProfilePhotoUrl()
        );

        // 3) 갱신값 재조회해서 응답으로 리턴
        UserEntity u = userJdbcRepository.findById(userId);
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
