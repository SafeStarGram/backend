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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserJdbcRepository userJdbcRepository;
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;


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

    /** 프로필 사진 업로드: 파일 저장 후 DB의 profile_photo_url 갱신하고 공개 URL을 반환 */
    public String uploadProfilePhoto(int userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 1) 사용자 존재 확인
        UserEntity exists = userJdbcRepository.findById(userId);
        if (exists == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        // 2) 저장 디렉토리 보장
        Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);

        // 3) 파일명 생성 (UUID + 원본 확장자 유지)
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.')); // 예: ".png"
        }
        String storedName = "profile_" + userId + "_" + UUID.randomUUID() + ext;

        // 4) 디스크 저장
        Path target = baseDir.resolve(storedName);
        Files.copy(file.getInputStream(), target);

        // 5) 공개 URL (정적 리소스 매핑 /uploads/**)
        String publicUrl = "/uploads/" + storedName;

        // 6) DB 업데이트
        userJdbcRepository.updateProfilePhoto(userId, publicUrl);

        return publicUrl;
    }

}
