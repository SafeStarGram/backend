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
import java.nio.file.StandardCopyOption;
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
                .profilePhotoUrl(convertToFullUrl(u.getProfilePhotoUrl()))
                .build();
    }

    // 추가: 내 프로필 수정
    @Transactional
    public ProfileResponse updateMyProfile(int userId, ProfileUpdateRequest req) {
        UserEntity exists = userJdbcRepository.findById(userId);
        if (exists == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        userJdbcRepository.updateProfile(
                userId,
                req.getPhoneNumber(),
                req.getRadioNumber(),
                req.getProfilePhotoUrl()
        );

        UserEntity u = userJdbcRepository.findById(userId);
        return ProfileResponse.builder()
                .userId(u.getUserId())
                .name(u.getName())
                .phoneNumber(u.getPhoneNumber())
                .radioNumber(u.getRadioNumber())
                .profilePhotoUrl(convertToFullUrl(u.getProfilePhotoUrl()))
                .build();
    }

    /** (기존) 사진만 업로드 — 내부 공통 저장 함수를 사용하도록 보완 */
    public String uploadProfilePhoto(int userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }
        UserEntity exists = userJdbcRepository.findById(userId);
        if (exists == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        String publicUrl = saveProfilePhoto(userId, file); // ★ 공통 함수 사용
        userJdbcRepository.updateProfilePhoto(userId, publicUrl);
        return publicUrl;
    }

    /** ✅ 통합: JSON(프로필) + 파일(선택) 한 번에 처리 */
    @Transactional
    public ProfileResponse updateMyProfileAndPhoto(int userId, ProfileUpdateRequest req, MultipartFile file) throws IOException {
        UserEntity exists = userJdbcRepository.findById(userId);
        if (exists == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        // 1) 파일이 왔다면 먼저 저장하고 URL 갱신
        String publicUrlFromFile = null;
        if (file != null && !file.isEmpty()) {
            publicUrlFromFile = saveProfilePhoto(userId, file);
            userJdbcRepository.updateProfilePhoto(userId, publicUrlFromFile);
        }

        // 2) JSON이 왔다면 텍스트 필드 갱신 (파일 없으면 req의 URL 사용)
        if (req != null) {
            String finalPhotoUrl = (publicUrlFromFile != null) ? publicUrlFromFile : req.getProfilePhotoUrl();
            userJdbcRepository.updateProfile(
                    userId,
                    req.getPhoneNumber(),
                    req.getRadioNumber(),
                    finalPhotoUrl
            );
        }

        // 3) 최종 상태 재조회 후 반환 (메서드 안에서 builder 사용)
        UserEntity u = userJdbcRepository.findById(userId);
        return ProfileResponse.builder()
                .userId(u.getUserId())
                .name(u.getName())
                .phoneNumber(u.getPhoneNumber())
                .radioNumber(u.getRadioNumber())
                .profilePhotoUrl(convertToFullUrl(u.getProfilePhotoUrl()))
                .build();
    }

    /** 내부 공통: 실제 파일 저장 + 공개 URL 생성 */
    private String saveProfilePhoto(int userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String storedName = "profile_" + userId + "_" + UUID.randomUUID() + ext;
        Path target = baseDir.resolve(storedName);

        // ★ 같은 이름 우연 충돌 방지: 덮어쓰기 옵션 추가
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // 완전한 URL로 반환 (도메인 + 경로)
        return "https://chan23.duckdns.org/safe_api/uploads/" + storedName;
    }

    /** 상대 경로를 완전한 URL로 변환 */
    private String convertToFullUrl(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            return null;
        }
        
        // 이미 완전한 URL인 경우 그대로 반환
        if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
            return photoUrl;
        }
        
        // 상대 경로인 경우 완전한 URL로 변환
        if (photoUrl.startsWith("/uploads/")) {
            return "https://chan23.duckdns.org/safe_api" + photoUrl;
        }
        
        // 다른 형태의 경로인 경우 기본 도메인 추가
        return "https://chan23.duckdns.org/safe_api/uploads/" + photoUrl;
    }

}
